package com.scottlogic.training.order;

import java.util.Date;
import java.util.UUID;


public class Order {

    private int quantity;
    private int price;
    private boolean isSellOrder;
    private UUID orderId;
    private Date date;


    public Order(int quantity, int price, boolean isSellOrder) {
        this.quantity = quantity;
        this.price = price;
        this.isSellOrder = isSellOrder;
        this.orderId = UUID.randomUUID();
        this.date = new Date();
    }

    public Order(int quantity, int price, boolean isSellOrder, UUID orderId) {
        this.quantity = quantity;
        this.price = price;
        this.isSellOrder = isSellOrder;
        this.orderId = orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public boolean getIsSellOrder() {
        return isSellOrder;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public Date getDate() {
        return date;
    }

    public void setQuantity(int newQuantity) { this.quantity = newQuantity; }
}