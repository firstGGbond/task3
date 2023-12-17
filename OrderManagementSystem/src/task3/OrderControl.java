package task3;
import java.util.ArrayList;
import java.util.Scanner;
public class OrderControl {
	GoodControl goodControl = new GoodControl();

    //�����ݿ��е�����Ʒ��Ϣ
    public ArrayList<Good> outGoodsInfo(){
        return goodControl.outGoods();
    }

    //�����ݿ��е���������Ϣ
    public ArrayList<Order> outOrdersInfo(ArrayList<Good> list){
        return goodControl.outOrders(list);
    }

    //������Ʒ����
    public ArrayList<Good> insertGoodInfo(ArrayList<Good> list){
        Scanner sc = new Scanner(System.in);
        System.out.println("�����뵼����Ʒ������");
        String name = sc.next();
        System.out.println("�����뵼����Ʒ�ļ۸� ");
        int price = sc.nextInt();
        return goodControl.insertGood(list,name,price);
    }

    //ɾ����Ʒ
    public ArrayList<Good> deleteGoodInfo(ArrayList<Good> list){
        Scanner sc = new Scanner(System.in);
        System.out.println("��������Ҫɾ����Ʒ������");
        String name = sc.next();
        return goodControl.deleteGood(list,name);
    }

    //���붩��
    public ArrayList<Order> insertOrderInfo(ArrayList<Good> list, ArrayList<Order>orders){
        return goodControl.insertOrder(list,orders);
    }

    //��ѯ��������
    public void queryOneOrderInfo(){
        Scanner sc = new Scanner(System.in);
        System.out.println("��������Ҫ��ѯ�Ķ���id");
        int id = sc.nextInt();
        goodControl.queryOneOrder(id);
    }

    //��ѯȫ������
    public void queryOrderInfo(){
    	goodControl.queryOrder();
    }

    //����ĳ�����е���Ʒ
    public ArrayList<Order> insertOrderGoodInfo(ArrayList<Order> orders, ArrayList<Good> goods){
        Scanner sc = new Scanner(System.in);
        System.out.println("��������Ҫ�޸ĵĶ���id");
        int id = sc.nextInt();
        System.out.println("��������Ҫ�������Ʒ����");
        String name = sc.next();
        return goodControl.insertOrderGood(orders,goods,id,name);
    }

    //ɾ��ĳ��������Ʒ
    public void deleteOrderGoodInfo(ArrayList<Order> orders, ArrayList<Good> goods) {
        Scanner sc = new Scanner(System.in);
        System.out.println("��������Ҫɾ��������id");
        int id = sc.nextInt();
        System.out.println("��������Ҫɾ������Ʒ����");
        String name = sc.next();
        goodControl.deleteOrderGood(orders,goods,id,name);
    }

    //��ѯ������Ʒ��Ϣ
    public void queryGoodInfo(){
    	goodControl.queryGood();
    }

    //���¶����м۸�
    public ArrayList<Order> updatePriceInfo(ArrayList<Order>orders, ArrayList<Good> goods) {
        Scanner sc = new Scanner(System.in);
        return goodControl.updatePrice(orders,goods);
    }


}
