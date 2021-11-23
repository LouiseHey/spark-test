package com.scottlogic.training.orders;

/*
 *  This is a Data transfer object which carries the message from client to server and vice-versa.
 *  It's used by the OrderModule to serialize and deserialize messages from and for the client.
 *
 *  Add/remove fields (and types) from here as necessary in order to match your message format.
 */
public class OrderDTO {

    private String userName;
    private String orderType;
    private int quantity;
    private int price;

    public OrderDTO() {
    }

    public OrderDTO(String userName, String orderType, int quantity, int price) {
        super();
        this.userName = userName;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrderType() {
        return orderType;
    }
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "userName='" + userName + '\'' +
                ", orderType='" + orderType + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
