package task3;
import java.util.*;

public class Test {
    public static void main(String[] args) {
        OrderControl orderControl = new OrderControl();
        Scanner sc = new Scanner(System.in);
        //�����ݿ��е�����Ʒ��Ϣ
        ArrayList<Good> goods = orderControl.outGoodsInfo();
        //�����ݿ��е���������Ϣ
        ArrayList<Order> orders = orderControl.outOrdersInfo(goods);
        while (true) {
            System.out.println("������������еĲ���: " + "\r\n"
                    + "1: ������Ʒ" + "\r\n"
                    + "2: ɾ����Ʒ" + "\r\n"
                    + "3: ���붩��" + "\r\n"
                    + "4: ��ѯȫ������" + "\r\n"
                    + "5: ���Ӷ�������Ʒ" + "\r\n"
                    + "6: ɾ����������Ʒ" + "\r\n"
                    + "7: ��ѯ������Ʒ" + "\r\n"
                    + "8: �˳�");

            int a = sc.nextInt();
            switch (a) {
                case 1:
                    System.out.println("���ڽ��в�����Ʒ");
                    orderControl.insertGoodInfo(goods);
                case 2:
                    System.out.println("���ڽ���ɾ����Ʒ");
                    orderControl.deleteGoodInfo(goods);
                case 3:
                    System.out.println("���ڽ��в��붩��");
                    orderControl.insertOrderInfo(goods,orders);
                case 4:
                    System.out.println("���ڽ��в�ѯȫ������");
                    orderControl.queryOrderInfo();
                case 5:
                    System.out.println("���ڽ������Ӷ�������Ʒ");
                    orderControl.insertOrderGoodInfo(orders,goods);
                case 6:
                    System.out.println("���ڽ���ɾ����������Ʒ");
                    orderControl.deleteOrderGoodInfo(orders,goods);
                case 7:
                    System.out.println("���ڽ��в�ѯ������Ʒ");
                    orderControl.queryGoodInfo();
                case 8:
                    System.exit(0);
                    System.out.println("�˳��ɹ�");
                default:  System.out.println("�������ѡ������,����������");
            }
        }
    }

}