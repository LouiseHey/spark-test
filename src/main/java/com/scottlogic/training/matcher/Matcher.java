package com.scottlogic.training.matcher;

import java.util.ArrayList;
import java.util.UUID;
import java.lang.Math;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.scottlogic.training.orders.OrderDTO;
import com.scottlogic.training.trade.Trade;
import com.scottlogic.training.order.Order;
import org.springframework.beans.factory.annotation.Autowired;

public class Matcher {

    private SparkSession sparkSession;

    private ArrayList<Order> buyOrders;
    private ArrayList<Order> sellOrders;
    private ArrayList<Trade> trades;

    @Autowired
    public Matcher(SparkSession sparkSession) {

        this.sparkSession = sparkSession;

        this.createTables();

//        Dataset buyOrders = sparkSession.sql("SELECT * FROM orders WHERE isSellOrder = 'false'");

        this.buyOrders = new ArrayList<>();
        this.sellOrders = new ArrayList<>();
        this.trades = new ArrayList<>();
    }

    public ArrayList<Order> getBuyOrders() {
        return buyOrders;
    }

    public ArrayList<Order> getSellOrders() {
        return sellOrders;
    }

    public ArrayList<Trade> getTrades() {
        return trades;
    }

    public void createTables() {
        if (!sparkSession.catalog().tableExists("orders")) {
            this.sparkSession.sql("CREATE TABLE IF NOT EXISTS orders (" +
                "orderId STRING, isSellOrder STRING, price INT, quantity INT" +
                ") USING hive");
        }

        if (!sparkSession.catalog().tableExists("trades")) {
            this.sparkSession.sql("CREATE TABLE IF NOT EXISTS trades (" +
                "buyId STRING, SelId STRING, totalPrice INT, quantity INT" +
                ") USING hive");
        }
    }

    public ArrayList<Order> mapDatasetToOrderList(Dataset df) {
        ArrayList<Order> orders = null;
        Row[] rows = (Row[]) df.collect();

        for (Row row : rows) {
            orders.add(new Order(row.getInt(3), row.getInt(2), row.getBoolean(1), (UUID) row.get(0)));
        }

        return orders;
    }

    public void processNewOrder(OrderDTO orderDTO) {

        boolean isSellOrder;
        isSellOrder = orderDTO.getOrderType().equals("sell");
        System.out.println(orderDTO.getOrderType());
        Order order = new Order(orderDTO.getQuantity(), orderDTO.getPrice(), isSellOrder);

        Order orderAfterMatch;
        if (order.getIsSellOrder()) {
            orderAfterMatch = makeTrades(order, this.buyOrders);
            addToOrderList(orderAfterMatch, this.sellOrders);
        } else {
            orderAfterMatch = makeTrades(order, this.sellOrders);
            addToOrderList(orderAfterMatch, this.buyOrders);
        }
    }

    private Order makeTrades(Order newOrder, ArrayList<Order> orderList) {
        for (Order order : orderList) {
            if (this.isMatch(newOrder, order) && newOrder.getQuantity() != 0) {
                Trade newTrade = this.createTrade(newOrder, order);
                this.trades.add(newTrade);

                newOrder.setQuantity(newOrder.getQuantity() - newTrade.getQuantity());
                order.setQuantity(order.getQuantity() - newTrade.getQuantity());
            } else {
                break;
            }
        }
        orderList.removeIf( order -> order.getQuantity() == 0 );
        return newOrder;
    }

    private boolean isMatch(Order newOrder, Order existingOrder) {
        if (newOrder.getIsSellOrder() && !existingOrder.getIsSellOrder() && newOrder.getPrice() <= existingOrder.getPrice()) {
            return true;
        } else if (!newOrder.getIsSellOrder() && existingOrder.getIsSellOrder()&& newOrder.getPrice() >= existingOrder.getPrice()) {
            return true;
        }
        return false;
    }

    private Trade createTrade(Order newOrder, Order existingOrder) {
        UUID sellId;
        UUID buyId;

        if (newOrder.getIsSellOrder()) {
            sellId = newOrder.getOrderId();
            buyId = existingOrder.getOrderId();
        } else {
            sellId = existingOrder.getOrderId();
            buyId = newOrder.getOrderId();
        }

        int quantity = Math.min(newOrder.getQuantity(), existingOrder.getQuantity());
        int totalPrice = quantity * existingOrder.getPrice();

        Trade trade = new Trade(quantity, totalPrice, buyId, sellId);

        this.sparkSession.sql(
            String.format(
                "INSERT INTO trades VALUES ('%s', '%s', %s, %s)",
                trade.getBuyOrderId(),
                trade.getSellOrderId(),
                trade.getTotalPrice(),
                trade.getQuantity()
            )
        );

        this.sparkSession.sql("SELECT * FROM trades").show();

        return new Trade(quantity, totalPrice, buyId, sellId);
    }

    private void addToOrderList(Order order, ArrayList<Order> orderList) {
        if (order.getQuantity() != 0) {
            int index = 0;

            if (order.getIsSellOrder()) {
                for (Order o : orderList) {
                    if (order.getPrice() > o.getPrice()) {
                        index++;
                    } else {
                        break;
                    }
                }
            } else {
                for (Order o : orderList) {
                    if (order.getPrice() < o.getPrice()) {
                        index++;
                    } else {
                        break;
                    }
                }
            }
            orderList.add(index, order);

            this.sparkSession.sql(
                    String.format(
                            "INSERT INTO orders VALUES ('%s', '%s', %s, %s)",
                            order.getOrderId(),
                            order.getIsSellOrder(),
                            order.getPrice(),
                            order.getQuantity()
                    )
            );
        }

        this.sparkSession.sql("SELECT * FROM orders").show();
    }
}