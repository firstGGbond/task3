package task3;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
public class GoodControl {
    //���ݿ�㷽��
    //�����ݿ��е�����Ʒ��Ϣ
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

            //���ò�ѯ����Ʒ��������Ʒ��ص�arrayList
            while (rs.next()) {
                Good g = new Good();
                g.setId(rs.getInt(1));
                g.setName(rs.getString(2));
                g.setPrice(rs.getInt(3));
                goods.add(g);
            }

            //��ӡ����������Ʒ
            System.out.println("����ӵ�е���ƷΪ��" + goods);
            return goods;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            //�ͷ�����
            JdbcUtil.release(conn, ps, rs);
        }
    }

    //�����ݿ��е���������Ϣ
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
            	����ѯ������Ϣ���붯̬����,
            	���ڽ���ͬ�����е���Ʒ����ͬһ��good��������,Ϊ�˴����ÿ��������ص�Order��ʵ����������
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
            /*����good��ʵ��������order���ʵ����������,
                              �ٽ��䵼��Order��Ķ�̬������*/
            for (int i = 0; i < l.size(); i++) {
                int orderId = (int) l.get(i)[0];
                int goodId = (int) l.get(i)[1];
                String orderTime = (String) l.get(i)[2];
                int orderPrice = (int) l.get(i)[3];

                // �洢��Ʒ����g
                for (int i1 = 0; i1 < list.size(); i1++) {
                    if (goodId == list.get(i1).getId()) {
                        g.add(list.get(i1));
                    }
                }
                /*
                	���ǰ��id��ͬ��˵����һ��ѭ��ʱ,��Ʒ���������Ķ���
                	(Ϊ�˷�ֹ�������鳤�ȷ�Χ,���������)
                	��� i != l.size()-1 ��Ƚ����һ���Ƿ�Ϊ��ͬ��Ʒ
                	������ͬ,����good������鵽order��ʵ��������Ķ�̬������;
                	��� i = l.size()-1 ��Ƚ�����ǰһ���Ƿ�����ͬ�����е���Ʒ.
                	������ͬ,���赼���µ�good������鵽order��ʵ��������Ķ�̬������;
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
                        //����һ����Ʒ��Ϊͬһ������ǰ,�������ർ��orders��̬�����,��Orderʵ����������good�ද̬�������
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
            //��ӡ
            for (int i = 0; i < orders.size(); i++) {
                Order order1 = orders.get(i);
                Good[] goods = order1.getGoods();
                System.out.println("��ǰ���̶���Ϊ��");
                System.out.println("����id��" + order1.getId() + " �����۸�" + order1.getPrice() + " ����ʱ�䣺" + order1.getTime());
                System.out.println("��������Ʒ��ϢΪ��");
                for (int i1 = 0; i1 < goods.length; i1++) {
                    System.out.print("{ ��Ʒid��" + goods[i1].getId() + " ��Ʒ���ƣ�" + goods[i1].getName() + " ��Ʒ�۸�" + goods[i1].getPrice() + " } ");
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
            //��ȡ�����Ʒid���,Ϊ����һ����Ʒ����ʱ������������
            String s1 = "SELECT MAX(good_id) FROM goods";
            String s = "INSERT INTO goods (good_id,good_name,good_price) VALUES(?,?,?)";
            ps = JdbcUtil.getPreparedStatement(s, conn);
            ps1 = JdbcUtil.getPreparedStatement(s1, conn);
            rs = ps1.executeQuery();
            int id = 0;
            //��ȡ������Ʒ��id
            while (rs.next()) {
                id = rs.getInt(1) + 1;
                System.out.println("������ƷidΪ " + id);
            }
            ps.setInt(1, id);
            Scanner sc = new Scanner(System.in);
            //ʵ����Good����,Ϊ�˸���Good��Ķ�̬����
            Good g = new Good();
            System.out.println("������Ʒ����Ϊ" + name);
            ps.setString(2, name);
            g.setName(name);
            while (true) {
                if (price > 0) {
                    System.out.println("�������Ʒ�۸�Ϊ " + price);
                    g.setPrice(price);
                    ps.setInt(3, price);
                    break;
                } else {
                    System.out.println("���������Ʒ�۸����������µ���۸�");
                    price = sc.nextInt();
                }
            }
            ps.executeUpdate();
            g.setId(id);
            System.out.println("����ɹ�");
            //���������Ʒ�����Ʒ����Ϣ
            System.out.println("������Ʒ��ϢΪ");
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
            //�ж��Ƿ��ҵ�����Ҫɾ������Ʒ
            if (index == 0) {
                System.out.println("û���ҵ�����ɾ������Ʒ");
            } else {
                ps.setString(1, name);
                ps.executeUpdate();
                System.out.println("ɾ���ɹ�");
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

            /*��ȡ��󶩵���id,Ϊ�˲���ʱ�����ſ�����������*/
            int sum = 0, id = 0;
            String s1 = "SELECT MAX(order_id) FROM orders";
            PreparedStatement ps1 = JdbcUtil.getPreparedStatement(s1, conn);
            ResultSet rs = ps1.executeQuery();
            while (rs.next()) {
                id = rs.getInt(1) + 1;
            }

            /*ͨ��ѭ�����ϵػ�ȡ��Ҫ�������Ʒ,���ͨ������'e'�˳�ѡ��*/
            System.out.println("��������Ҫ������Ʒ������");
            a:
            while (true) {
                String name = sc.next();
                /*����ǰ��ʱ��ת��Ϊstring����ʽ*/
                Date currentTime = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = formatter.format(currentTime);
                System.out.println(dateString);

                boolean flog = false;
                /*�ж���Ʒ�б����Ƿ��й˿���Ҫ�������Ʒ*/
                for (int i = 0; i < list.size(); i++) {
                    Good g = list.get(i);
                    if (g.getName().equals(name)) {
                        sum += g.getPrice();
                        list1.add(g);
                        flog = true;
                        System.out.println("�ɹ�����Ʒ���붩���У��������ѡ���������������Ʒ���ƣ����� e �˳�ѡ����");
                        break;
                    }
                }
                /*���������������ݿ���*/
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
                    System.out.println("δ�ҵ�����Ҫѡ������Ʒ���������ѡ���������������Ʒ���ƣ����� e �˳�ѡ����");
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
            /*ͨ�������ѯ�ķ���,ͬʱ��ȡȫ���Ķ�������Ʒ����Ϣ*/
            String s = "SELECT `order_id`,o.good_id,`good_name`,`good_price`,`order_time`,`order_price`\n" +
                    "from orders as o\n" +
                    "left join goods as g\n" +
                    " on o.good_id = g.good_id;" ;
            rs = JdbcUtil.executeQuery(conn, s, id);
            while (rs.next()) {
                System.out.print("������ϢΪ��");
                System.out.println(" �����ţ�" + rs.getInt("order_id") + " �µ�ʱ�䣺" + rs.getDate("order_time") + " �����۸�" + rs.getString("order_price"));
                System.out.print("��������Ʒ��ϢΪ��");
                System.out.println(" ��Ʒid��" + rs.getInt("good_id") + " ��Ʒ���ƣ�" + rs.getString("good_name") + " ��Ʒ���ۣ�" + rs.getInt("good_price"));
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

            /*ͨ�������ѯ�ķ���,ͬʱ��ȡȫ���Ķ�������Ʒ����Ϣ*/
            String s = "SELECT `order_id`,o.good_id,`good_name`,`good_price`,`order_time`,`order_price`\n" +
                    "from orders as o\n" +
                    "left join goods as g\n" +
                    "on o.good_id = g.good_id";
            rs = JdbcUtil.executeQuery(conn, s);
            while (rs.next()) {
                System.out.print("������ϢΪ��");
                System.out.println(" �����ţ�" + rs.getInt("order_id") + " �µ�ʱ�䣺" + rs.getDate("order_time") + " �����۸�" + rs.getString("order_price"));
                System.out.print("��������Ʒ��ϢΪ��");
                System.out.println(" ��Ʒid��" + rs.getInt("good_id") + " ��Ʒ���ƣ�" + rs.getString("good_name") + " ��Ʒ���ۣ�" + rs.getInt("good_price"));
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
            /*����Ʒ�����ҵ���Ʒ*/
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
                System.out.println("���������Ʒ����");
                return null;
            }
            /*�ڶ��������ҵ�����*/
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getId() == id) {
                    order = orders.get(i);
                    flag1 = true;
                }
            }
            if (!flag1) {
                System.out.println("������Ķ�������");
                return null;
            }
            int goodId = good.getId();
            String time = order.getTime();
            /*��Ӽ������Ʒ��������*/
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
            /*����궩���е���Ʒ��,���¼۸�*/
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
                System.out.println("���������Ʒ����");
                return null;
            }
            if (!flag1) {
                System.out.println("������Ķ�������");
                return null;
            }
            GoodControl goodsDaoOrder = new GoodControl();
            /*ɾ���궩���е���Ʒ��,���¼۸�*/
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
            /*ͨ�������ѯ�ķ���,ͬʱ��ȡȫ���Ķ�������Ʒ����Ϣ*/
            String s = "SELECT good_id,good_name,good_price FROM goods ";
            rs = JdbcUtil.executeQuery(conn, s);
            while (rs.next()) {
                System.out.print("��������Ʒ��ϢΪ��");
                System.out.println(" ��Ʒid��" + rs.getInt("good_id") + " ��Ʒ���ƣ�" + rs.getString("good_name") + " ��Ʒ���ۣ�" + rs.getInt("good_price"));
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
                    /*ͨ��ѭ����ȡÿ�������е���Ʒ��*/
                    if (orders.get(j).getId() == i) {
                        Good[] goods = orders.get(j).getGoods();
                        /*����goods����Ʒֵ*/
                        for (int i1 = 0; i1 < goods.length; i1++) {
                            for (int i2 = 0; i2 < good.size(); i2++) {
                                if (good.get(i2).getId() == goods[i1].getId()) {
                                    goods[i1].setPrice(good.get(i2).getPrice());
                                }
                            }
                        }
                        /*����ȡ����Ʒ���۸��ܺ�*/
                        for (int i1 = 0; i1 < goods.length; i1++) {
                            price += goods[i1].getPrice();
                        }
                    }
                }
                /*�������ö����۸�*/
                for (int j = 0; j < orders.size(); j++) {
                    if (orders.get(j).getId() == i) {
                        orders.get(j).setPrice(price);
                    }
                }
                String sql = "UPDATE orders SET `order_price` = ? WHERE `order_id` = ?";
                JdbcUtil.executeUpdate(conn, sql, price, i);
            }
            System.out.println("�������ĳɹ�");
            return orders;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            JdbcUtil.release(conn, null, null);
        }
    }

}