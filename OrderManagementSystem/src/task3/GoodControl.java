package task3;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
public class GoodControl {
    //数据库层方法
    //从数据库中导出物品信息
    public ArrayList<Good> outGoods() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JdbcUtil.getConnection();
            ArrayList<Good> goods = new ArrayList<>();
            String s = "SELECT `good_id`,`good_name`,`good_price` FROM goods";
            ps = JdbcUtil.getPreparedStatement(s, conn);
            rs = ps.executeQuery();

            //运用查询将商品导入与商品相关的arrayList
            while (rs.next()) {
                Good g = new Good();
                g.setId(rs.getInt(1));
                g.setName(rs.getString(2));
                g.setPrice(rs.getInt(3));
                goods.add(g);
            }

            //打印店内所有商品
            System.out.println("店铺拥有的商品为：" + goods);
            return goods;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            //释放连接
            JdbcUtil.release(conn, ps, rs);
        }
    }

    //从数据库中导出订单信息
    public ArrayList<Order> outOrders(ArrayList<Good> list) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JdbcUtil.getConnection();
            String s = "SELECT `order_id`,`good_id`,`order_time`,`order_price` FROM orders ORDER BY order_id ASC";
            ps = JdbcUtil.getPreparedStatement(s, conn);
            rs = ps.executeQuery();
            ArrayList<Good> g = new ArrayList<>();
            ArrayList<Order> orders = new ArrayList<>();
            ArrayList<Object[]> l = new ArrayList<>();
            Order order = new Order();

            /*
            	将查询到的信息导入动态数组,
            	便于将相同订单中的物品放入同一个good类数组中,为了储存进每个订单相关的Order类实例化对象中
            */
            while (rs.next()) {
                Object[] object = new Object[4];
                object[0] = rs.getInt(1);
                object[1] = rs.getInt(2);
                object[2] = String.valueOf(rs.getDate(3));
                object[3] = rs.getInt(4);
                l.add(object);
            }
            System.out.println(l.size());
            /*导入good类实例化对象到order类的实例化对象中,
                              再将其导入Order类的动态数组中*/
            for (int i = 0; i < l.size(); i++) {
                int orderId = (int) l.get(i)[0];
                int goodId = (int) l.get(i)[1];
                String orderTime = (String) l.get(i)[2];
                int orderPrice = (int) l.get(i)[3];

                // 存储商品进入g
                for (int i1 = 0; i1 < list.size(); i1++) {
                    if (goodId == list.get(i1).getId()) {
                        g.add(list.get(i1));
                    }
                }
                /*
                	如果前后id不同，说明下一次循环时,商品来自其他的订单
                	(为了防止超出数组长度范围,分两种情况)
                	如果 i != l.size()-1 则比较其后一个是否为相同商品
                	若不相同,则导入good类的数组到order类实例化对象的动态数组中;
                	如果 i = l.size()-1 则比较其与前一个是否是相同订单中的商品.
                	若不相同,则需导入新的good类的数组到order类实例化对象的动态数组中;
                 */
                if (i != l.size() - 1) {
                    if (orderId != (int) l.get(i + 1)[0]) {
                        Good[] goods = new Good[g.size()];
                        for (int i1 = 0; i1 < goods.length; i1++) {
                            goods[i1] = g.get(i1);
                        }
                        order.setId(orderId);
                        order.setGoods(goods);
                        order.setTime(orderTime);
                        order.setPrice(orderPrice);
                        orders.add(order);
                        //在下一个物品不为同一个订单前,将订单类导入orders动态数组后,将Order实例化对象与good类动态数组清空
                        order = new Order();
                        g = new ArrayList<>();

                    }
                } else {
                    Good[] goods = new Good[g.size()];
                    for (int i1 = 0; i1 < goods.length; i1++) {
                        goods[i1] = g.get(i1);
                    }
                    order.setId(orderId);
                    order.setGoods(goods);
                    order.setTime(orderTime);
                    order.setPrice(orderPrice);
                    orders.add(order);
                }
            }
            //打印
            for (int i = 0; i < orders.size(); i++) {
                Order order1 = orders.get(i);
                Good[] goods = order1.getGoods();
                System.out.println("当前店铺订单为：");
                System.out.println("订单id：" + order1.getId() + " 订单价格：" + order1.getPrice() + " 订单时间：" + order1.getTime());
                System.out.println("订单中商品信息为：");
                for (int i1 = 0; i1 < goods.length; i1++) {
                    System.out.print("{ 商品id：" + goods[i1].getId() + " 商品名称：" + goods[i1].getName() + " 商品价格：" + goods[i1].getPrice() + " } ");
                }
                System.out.println("");
            }
            return orders;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            JdbcUtil.release(conn, ps, rs);
        }
    }

    public ArrayList<Good> insertGood(ArrayList<Good> list, String name, int price) {
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        try {
            conn = JdbcUtil.getConnection();
            //获取最大商品id序号,为了下一个商品插入时可以往后延续
            String s1 = "SELECT MAX(good_id) FROM goods";
            String s = "INSERT INTO goods (good_id,good_name,good_price) VALUES(?,?,?)";
            ps = JdbcUtil.getPreparedStatement(s, conn);
            ps1 = JdbcUtil.getPreparedStatement(s1, conn);
            rs = ps1.executeQuery();
            int id = 0;
            //获取导入商品的id
            while (rs.next()) {
                id = rs.getInt(1) + 1;
                System.out.println("导入商品id为 " + id);
            }
            ps.setInt(1, id);
            Scanner sc = new Scanner(System.in);
            //实例化Good对象,为了更新Good类的动态数组
            Good g = new Good();
            System.out.println("导入商品名称为" + name);
            ps.setString(2, name);
            g.setName(name);
            while (true) {
                if (price > 0) {
                    System.out.println("导入的商品价格为 " + price);
                    g.setPrice(price);
                    ps.setInt(3, price);
                    break;
                } else {
                    System.out.println("您导入的商品价格有误，请重新导入价格");
                    price = sc.nextInt();
                }
            }
            ps.executeUpdate();
            g.setId(id);
            System.out.println("导入成功");
            //输出插入商品后的商品总信息
            System.out.println("导入商品信息为");
            System.out.println(g);
            list.add(g);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            JdbcUtil.release(conn, ps, rs);
            if (ps1 != null) {
                try {
                    ps1.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public ArrayList<Good> deleteGood(ArrayList<Good> list, String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JdbcUtil.getConnection();
            String s = "delete from goods where good_name = ?";
            ps = JdbcUtil.getPreparedStatement(s, conn);
            int index = 0;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().equals(name)) {
                    index = 1;
                    list.remove(i);
                }
            }
            //判断是否找到了想要删除的商品
            if (index == 0) {
                System.out.println("没有找到你想删除的商品");
            } else {
                ps.setString(1, name);
                ps.executeUpdate();
                System.out.println("删除成功");
                ps = null;
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            JdbcUtil.release(conn, ps, null);
        }
    }

    public ArrayList<Order> insertOrder(ArrayList<Good> list, ArrayList<Order> orders) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            Scanner sc = new Scanner(System.in);
            conn = JdbcUtil.getConnection();
            Order order = new Order();
            ArrayList<Good> list1 = new ArrayList<>();

            /*获取最大订单号id,为了插入时订单号可以向下延续*/
            int sum = 0, id = 0;
            String s1 = "SELECT MAX(order_id) FROM orders";
            PreparedStatement ps1 = JdbcUtil.getPreparedStatement(s1, conn);
            ResultSet rs = ps1.executeQuery();
            while (rs.next()) {
                id = rs.getInt(1) + 1;
            }

            /*通过循环不断地获取想要购买的商品,最后通过输入'e'退出选购*/
            System.out.println("请输入想要购入商品的名称");
            a:
            while (true) {
                String name = sc.next();
                /*将当前的时间转化为string的形式*/
                Date currentTime = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = formatter.format(currentTime);
                System.out.println(dateString);

                boolean flog = false;
                /*判断商品列表中是否有顾客想要购买的商品*/
                for (int i = 0; i < list.size(); i++) {
                    Good g = list.get(i);
                    if (g.getName().equals(name)) {
                        sum += g.getPrice();
                        list1.add(g);
                        flog = true;
                        System.out.println("成功将商品加入订单中，如需继续选购，请继续输入商品名称（输入 e 退出选购）");
                        break;
                    }
                }
                /*将增添订单导入数据库中*/
                if (name.equals("e")) {
                    Good[] goods = new Good[list1.size()];
                    for (int i = 0; i < list1.size(); i++) {
                        goods[i] = list1.get(i);
                        java.sql.Date date = new java.sql.Date(currentTime.getTime());
                        String s = "INSERT INTO orders (`order_id`,`good_id`,`order_time`,`order_price`) \n" +
                                "VALUES(" + id + "," + goods[i].getId() + ",'" + date + "'," + sum + ")";
                        ps = JdbcUtil.getPreparedStatement(s, conn);
                        ps.executeUpdate();

                    }
                    order.setGoods(goods);
                    order.setTime(dateString);
                    order.setPrice(sum);
                    order.setId(id);
                    orders.add(order);
                    return orders;
                } else if (!flog) {
                    System.out.println("未找到您想要选购的商品，如需继续选购，请继续输入商品名称（输入 e 退出选购）");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            JdbcUtil.release(conn, ps, null);
        }
    }

    public void queryOneOrder(int id) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = JdbcUtil.getConnection();
            /*通过联表查询的方法,同时获取全部的订单和商品的信息*/
            String s = "SELECT `order_id`,o.good_id,`good_name`,`good_price`,`order_time`,`order_price`\n" +
                    "from orders as o\n" +
                    "left join goods as g\n" +
                    " on o.good_id = g.good_id;" ;
            rs = JdbcUtil.executeQuery(conn, s, id);
            while (rs.next()) {
                System.out.print("订单信息为：");
                System.out.println(" 订单号：" + rs.getInt("order_id") + " 下单时间：" + rs.getDate("order_time") + " 订单价格：" + rs.getString("order_price"));
                System.out.print("订单的商品信息为：");
                System.out.println(" 商品id：" + rs.getInt("good_id") + " 商品名称：" + rs.getString("good_name") + " 商品单价：" + rs.getInt("good_price"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.release(conn, null, rs);
        }
    }

    public void queryOrder() {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = JdbcUtil.getConnection();

            /*通过联表查询的方法,同时获取全部的订单和商品的信息*/
            String s = "SELECT `order_id`,o.good_id,`good_name`,`good_price`,`order_time`,`order_price`\n" +
                    "from orders as o\n" +
                    "left join goods as g\n" +
                    "on o.good_id = g.good_id";
            rs = JdbcUtil.executeQuery(conn, s);
            while (rs.next()) {
                System.out.print("订单信息为：");
                System.out.println(" 订单号：" + rs.getInt("order_id") + " 下单时间：" + rs.getDate("order_time") + " 订单价格：" + rs.getString("order_price"));
                System.out.print("订单的商品信息为：");
                System.out.println(" 商品id：" + rs.getInt("good_id") + " 商品名称：" + rs.getString("good_name") + " 商品单价：" + rs.getInt("good_price"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.release(conn, null, rs);
        }
    }

    public ArrayList<Order> insertOrderGood(ArrayList<Order> orders, ArrayList<Good> goods, int id, String name) {
        Connection conn = null;
        try {
            conn = JdbcUtil.getConnection();
            /*在商品堆中找到商品*/
            boolean flag = false, flag1 = false;
            Good good = new Good();
            Order order = new Order();
            for (int i = 0; i < goods.size(); i++) {
                if (goods.get(i).getName().equals(name)) {
                    good = goods.get(i);
                    flag = true;
                }
            }
            if (!flag) {
                System.out.println("您输入的商品有误");
                return null;
            }
            /*在订单堆中找到订单*/
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getId() == id) {
                    order = orders.get(i);
                    flag1 = true;
                }
            }
            if (!flag1) {
                System.out.println("您输入的订单有误");
                return null;
            }
            int goodId = good.getId();
            String time = order.getTime();
            /*添加加入的商品到订单中*/
            Good[] goods1 = order.getGoods();
            Good[] goods2 = new Good[goods1.length + 1];
            for (int i = 0; i < goods1.length; i++) {
                goods2[i] = goods1[i];
            }
            for (int i = 0; i < goods.size(); i++) {
                if (goods.get(i).getName().equals(name)) {
                    goods2[goods1.length] = goods.get(i);
                }
            }
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getId() == id) {
                    orders.get(i).setGoods(goods2);
                }
            }
            GoodControl goodsDaoOrder = new GoodControl();
            /*添加完订单中的商品后,更新价格*/
            String sql = "INSERT INTO `orders`(order_id,good_id,order_time,order_price)\n" +
                    "values (?,?,?,0)";
            JdbcUtil.executeUpdate(conn, sql, id, goodId, time);
            goodsDaoOrder.updatePrice(orders, goods);
            return orders;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            JdbcUtil.release(conn, null, null);
        }
    }

    public ArrayList<Order> deleteOrderGood(ArrayList<Order> orders, ArrayList<Good> goods, int id, String name) {
        Connection conn = null;
        try {
            boolean flag = false, flag1 = false;
            conn = JdbcUtil.getConnection();
            Order order;
            Good[] goods1 = new Good[0];
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getId() == id) {
                    flag1 = true;
                    order = orders.get(i);
                    goods1 = order.getGoods();
                    for (int i1 = 0; i1 < order.getGoods().length; i1++) {
                        if (orders.get(i).getGoods()[i1].getName().equals(name)) {
                            flag = true;
                            break;
                        }
                    }
                    break;
                }
            }
            Good[] goods2 = new Good[goods1.length - 1];
            for (int i = 0, j = 0; i < goods2.length; j++) {
                if (!goods1[j].getName().equals(name)) {
                    goods2[i] = goods1[j];
                    i++;
                }
            }
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getId() == id) {
                    orders.get(i).setGoods(goods2);
                }
            }
            if (!flag) {
                System.out.println("您输入的商品有误");
                return null;
            }
            if (!flag1) {
                System.out.println("您输入的订单有误");
                return null;
            }
            GoodControl goodsDaoOrder = new GoodControl();
            /*删除完订单中的商品后,更新价格*/
            goodsDaoOrder.updatePrice(orders, goods);
            int index = 0;
            for (int i = 0; i < goods.size(); i++) {
                if (goods.get(i).getName().equals(name)) {
                    index = i + 1;
                }
            }
            String sql = "DELETE FROM orders WHERE `order_id` = ? AND `good_id` = ?";
            JdbcUtil.executeUpdate(conn, sql, id, index);
            return orders;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            JdbcUtil.release(conn, null, null);
        }
    }

    public void queryGood() {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = JdbcUtil.getConnection();
            /*通过联表查询的方法,同时获取全部的订单和商品的信息*/
            String s = "SELECT good_id,good_name,good_price FROM goods ";
            rs = JdbcUtil.executeQuery(conn, s);
            while (rs.next()) {
                System.out.print("订单的商品信息为：");
                System.out.println(" 商品id：" + rs.getInt("good_id") + " 商品名称：" + rs.getString("good_name") + " 商品单价：" + rs.getInt("good_price"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.release(conn, null, rs);
        }
    }

    public ArrayList<Order> updatePrice(ArrayList<Order> orders, ArrayList<Good> good) {
        Connection conn = null;
        try {
            conn = JdbcUtil.getConnection();
            for (int i = 1; i <= orders.size(); i++) {
                int price = 0;
                for (int j = 0; j < orders.size(); j++) {
                    /*通过循环获取每个订单中的商品单*/
                    if (orders.get(j).getId() == i) {
                        Good[] goods = orders.get(j).getGoods();
                        /*设置goods的商品值*/
                        for (int i1 = 0; i1 < goods.length; i1++) {
                            for (int i2 = 0; i2 < good.size(); i2++) {
                                if (good.get(i2).getId() == goods[i1].getId()) {
                                    goods[i1].setPrice(good.get(i2).getPrice());
                                }
                            }
                        }
                        /*将获取的商品单价格总和*/
                        for (int i1 = 0; i1 < goods.length; i1++) {
                            price += goods[i1].getPrice();
                        }
                    }
                }
                /*重新设置订单价格*/
                for (int j = 0; j < orders.size(); j++) {
                    if (orders.get(j).getId() == i) {
                        orders.get(j).setPrice(price);
                    }
                }
                String sql = "UPDATE orders SET `order_price` = ? WHERE `order_id` = ?";
                JdbcUtil.executeUpdate(conn, sql, price, i);
            }
            System.out.println("订单更改成功");
            return orders;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            JdbcUtil.release(conn, null, null);
        }
    }

}