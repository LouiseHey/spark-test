package com.scottlogic.training.matcher;

import com.scottlogic.training.order.Order;
import com.scottlogic.training.trade.Trade;
import org.junit.Test;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class MatcherTest {
    @Test
    public void addsBuyOrder() {
        Matcher matcher = new Matcher();
        matcher.processNewOrder(100, 10, false);

        ArrayList<Order> sellOrders = matcher.getSellOrders();
        ArrayList<Order> buyOrders = matcher.getBuyOrders();

        assertEquals(sellOrders.size(), 0);

        assertEquals(buyOrders.size(), 1);
        assertEquals(buyOrders.get(0).getQuantity(), 100);
        assertEquals(buyOrders.get(0).getPrice(), 10);
        assertFalse(buyOrders.get(0).getIsSellOrder());
    }

    @Test
    public void addsSellOrder() {
        Matcher matcher = new Matcher();
        matcher.processNewOrder(100, 10, true);

        ArrayList<Order> sellOrders = matcher.getSellOrders();
        ArrayList<Order> buyOrders = matcher.getBuyOrders();

        assertEquals(sellOrders.size(), 1);
        assertEquals(sellOrders.get(0).getQuantity(), 100);
        assertEquals(sellOrders.get(0).getPrice(), 10);
        assertTrue(sellOrders.get(0).getIsSellOrder());

        assertEquals(buyOrders.size(), 0);
    }

    @Test
    public void exactBuyOrderMatches() {
        Matcher matcher = new Matcher();
        matcher.processNewOrder(100, 10, true);
        matcher.processNewOrder(100, 10, false);

        ArrayList<Order> sellOrders = matcher.getSellOrders();
        ArrayList<Order> buyOrders = matcher.getBuyOrders();
        ArrayList<Trade> trades = matcher.getTrades();

        assertEquals(sellOrders.size(), 0);
        assertEquals(buyOrders.size(), 0);

        assertEquals(trades.size(), 1);
        assertEquals(trades.get(0).getQuantity(), 100);
        assertEquals(trades.get(0).getTotalPrice(), 1000);
    }

    @Test
    public void exactSellOrderMatches() {
        Matcher matcher = new Matcher();
        matcher.processNewOrder(100, 10, false);
        matcher.processNewOrder(100, 10, true);

        ArrayList<Order> sellOrders = matcher.getSellOrders();
        ArrayList<Order> buyOrders = matcher.getBuyOrders();
        ArrayList<Trade> trades = matcher.getTrades();

        assertEquals(sellOrders.size(), 0);
        assertEquals(buyOrders.size(), 0);

        assertEquals(trades.size(), 1);
        assertEquals(trades.get(0).getQuantity(), 100);
        assertEquals(trades.get(0).getTotalPrice(), 1000);
    }

    @Test
    public void partialBuyOrderMatches() {
        Matcher matcher = new Matcher();
        matcher.processNewOrder(100, 10, true);
        matcher.processNewOrder(40, 20, false);

        ArrayList<Order> sellOrders = matcher.getSellOrders();
        ArrayList<Order> buyOrders = matcher.getBuyOrders();
        ArrayList<Trade> trades = matcher.getTrades();

        assertEquals(sellOrders.size(), 1);
        assertEquals(sellOrders.get(0).getQuantity(), 60);
        assertEquals(sellOrders.get(0).getPrice(), 10);
        assertTrue(sellOrders.get(0).getIsSellOrder());

        assertEquals(buyOrders.size(), 0);

        assertEquals(trades.size(), 1);
        assertEquals(trades.get(0).getQuantity(), 40);
        assertEquals(trades.get(0).getTotalPrice(), 400);
    }

    @Test
    public void partialSellOrderMatches() {
        Matcher matcher = new Matcher();
        matcher.processNewOrder(100, 20, false);
        matcher.processNewOrder(40, 10, true);

        ArrayList<Order> sellOrders = matcher.getSellOrders();
        ArrayList<Order> buyOrders = matcher.getBuyOrders();
        ArrayList<Trade> trades = matcher.getTrades();

        assertEquals(sellOrders.size(), 0);

        assertEquals(buyOrders.size(), 1);
        assertEquals(buyOrders.get(0).getQuantity(), 60);
        assertEquals(buyOrders.get(0).getPrice(), 20);
        assertFalse(buyOrders.get(0).getIsSellOrder());

        assertEquals(trades.size(), 1);
        assertEquals(trades.get(0).getQuantity(), 40);
        assertEquals(trades.get(0).getTotalPrice(), 800);
    }

    @Test
    public void insertsInRightOrderSell() {
        Matcher matcher = new Matcher();
        matcher.processNewOrder(1, 20, true);
        matcher.processNewOrder(1, 10, true);
        matcher.processNewOrder(1, 40, true);
        matcher.processNewOrder(1, 30, true);
        matcher.processNewOrder(2, 20, true);

        ArrayList<Order> sellOrders = matcher.getSellOrders();
        ArrayList<Order> buyOrders = matcher.getBuyOrders();
        ArrayList<Trade> trades = matcher.getTrades();

        assertEquals(sellOrders.size(), 5);
        assertEquals(sellOrders.get(0).getPrice(), 10);
        assertEquals(sellOrders.get(1).getPrice(), 20);
        assertEquals(sellOrders.get(1).getQuantity(), 2);
        assertEquals(sellOrders.get(2).getPrice(), 20);
        assertEquals(sellOrders.get(2).getQuantity(), 1);
        assertEquals(sellOrders.get(3).getPrice(), 30);
        assertEquals(sellOrders.get(4).getPrice(), 40);

        assertEquals(buyOrders.size(), 0);
        assertEquals(trades.size(), 0);
    }

    @Test
    public void insertsInRightOrderBuy() {
        Matcher matcher = new Matcher();
        matcher.processNewOrder(1, 20, false);
        matcher.processNewOrder(1, 10, false);
        matcher.processNewOrder(1, 40, false);
        matcher.processNewOrder(1, 30, false);
        matcher.processNewOrder(2, 20, false);

        ArrayList<Order> sellOrders = matcher.getSellOrders();
        ArrayList<Order> buyOrders = matcher.getBuyOrders();
        ArrayList<Trade> trades = matcher.getTrades();

        assertEquals(buyOrders.size(), 5);
        assertEquals(buyOrders.get(0).getPrice(), 40);
        assertEquals(buyOrders.get(1).getPrice(), 30);
        assertEquals(buyOrders.get(2).getQuantity(), 2);
        assertEquals(buyOrders.get(2).getPrice(), 20);
        assertEquals(buyOrders.get(3).getQuantity(), 1);
        assertEquals(buyOrders.get(3).getPrice(), 20);
        assertEquals(buyOrders.get(4).getPrice(), 10);

        assertEquals(sellOrders.size(), 0);
        assertEquals(trades.size(), 0);
    }

    @Test
    public void matchesMultipleBuyOrders() {
        Matcher matcher = new Matcher();
        matcher.processNewOrder(10, 20, false);
        matcher.processNewOrder(40, 15, false);
        matcher.processNewOrder(40, 10, true);

        ArrayList<Order> sellOrders = matcher.getSellOrders();
        ArrayList<Order> buyOrders = matcher.getBuyOrders();
        ArrayList<Trade> trades = matcher.getTrades();

        assertEquals(sellOrders.size(), 0);

        assertEquals(buyOrders.size(), 1);
        assertEquals(buyOrders.get(0).getQuantity(), 10);
        assertEquals(buyOrders.get(0).getPrice(), 15);

        assertEquals(trades.size(), 2);
        assertEquals(trades.get(0).getQuantity(), 10);
        assertEquals(trades.get(0).getTotalPrice(), 200);
        assertEquals(trades.get(1).getQuantity(), 30);
        assertEquals(trades.get(1).getTotalPrice(), 450);
    }

    @Test
    public void matchesMultipleSellOrders() {
        Matcher matcher = new Matcher();
        matcher.processNewOrder(10, 10, true);
        matcher.processNewOrder(40, 15, true);
        matcher.processNewOrder(40, 20, false);

        ArrayList<Order> sellOrders = matcher.getSellOrders();
        ArrayList<Order> buyOrders = matcher.getBuyOrders();
        ArrayList<Trade> trades = matcher.getTrades();

        assertEquals(buyOrders.size(), 0);

        assertEquals(sellOrders.size(), 1);
        assertEquals(sellOrders.get(0).getQuantity(), 10);
        assertEquals(sellOrders.get(0).getPrice(), 15);

        assertEquals(trades.size(), 2);
        assertEquals(trades.get(0).getQuantity(), 10);
        assertEquals(trades.get(0).getTotalPrice(), 100);
        assertEquals(trades.get(1).getQuantity(), 30);
        assertEquals(trades.get(1).getTotalPrice(), 450);
    }

}
