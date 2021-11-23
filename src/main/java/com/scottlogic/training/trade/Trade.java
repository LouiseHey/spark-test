package com.scottlogic.training.trade;

import java.util.Date;
import java.util.UUID;


public class Trade {

    private int quantity;
    private int totalPrice;
    private UUID buyOrderId;
    private UUID sellOrderId;
    private Date date;

    public Trade(int quantity, int totalPrice, UUID buyOrderId, UUID sellOrderId) {
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.date = new Date();
    }

    public int getQuantity() {
        return quantity;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public UUID getBuyOrderId() {
        return buyOrderId;
    }

    public UUID getSellOrderId() {
        return sellOrderId;
    }

    public Date getDate() {
        return date;
    }
}