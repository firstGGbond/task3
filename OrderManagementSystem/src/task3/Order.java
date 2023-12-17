package task3;

public class Order {
	private int id;
    private Good[]goods;
    private String time;
    private int price;

    public Order() {
    }

    public Order(int id, Good[] goods, String time, int price) {
        this.id = id;
        this.goods = goods;
        this.time = time;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Good[] getGoods() {
        return goods;
    }

    public void setGoods(Good[] goods) {
        this.goods = goods;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}
