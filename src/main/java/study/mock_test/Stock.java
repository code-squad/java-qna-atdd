package study.mock_test;

public class Stock {
    private String storkId;
    private String name;
    private int quantity;

    public Stock(String storkId, String name, int quantity) {
        this.storkId = storkId;
        this.name = name;
        this.quantity = quantity;
    }

    public String getStorkId() {
        return storkId;
    }

    public void setStorkId(String storkId) {
        this.storkId = storkId;
    }

    public String getTicker() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}
