package task3;
import java.util.*;

public class Test {
    public static void main(String[] args) {
        OrderControl orderControl = new OrderControl();
        Scanner sc = new Scanner(System.in);
        //从数据库中导出物品信息
        ArrayList<Good> goods = orderControl.outGoodsInfo();
        //从数据库中导出订单信息
        ArrayList<Order> orders = orderControl.outOrdersInfo(goods);
        boolean flag =true;
        while (flag) {
            System.out.println("请输入你想进行的操作: " + "\r\n"
                    + "1: 插入商品" + "\r\n"
                    + "2: 删除商品" + "\r\n"
                    + "3: 插入订单" + "\r\n"
                    + "4: 查询全部订单" + "\r\n"
                    + "5: 增加订单中商品" + "\r\n"
                    + "6: 删除订单中商品" + "\r\n"
                    + "7: 查询所有商品" + "\r\n"
                    + "8: 退出");

            int a = sc.nextInt();
            switch (a) {
                case 1:
                    System.out.println("正在进行插入商品");
                    orderControl.insertGoodInfo(goods);
                    break;
                case 2:
                    System.out.println("正在进行删除商品");
                    orderControl.deleteGoodInfo(goods);
                    break;
                case 3:
                    System.out.println("正在进行插入订单");
                    orderControl.insertOrderInfo(goods,orders);
                    break;
                case 4:
                    System.out.println("正在进行查询全部订单");
                    orderControl.queryOrderInfo();
                    break;
                case 5:
                    System.out.println("正在进行增加订单中商品");
                    orderControl.insertOrderGoodInfo(orders,goods);
                    break;
                case 6:
                    System.out.println("正在进行删除订单中商品");
                    orderControl.deleteOrderGoodInfo(orders,goods);
                    break;
                case 7:
                    System.out.println("正在进行查询所有商品");
                    orderControl.queryGoodInfo();
                    break;
                case 8:
                    System.exit(0);
                    System.out.println("退出成功");
                    flag = false;
                    break;
                default:  System.out.println("您输入的选项有误,请重新输入");
            }
        }
    }

}