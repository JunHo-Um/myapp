package com.example.upbit;

import com.fasterxml.jackson.databind.JsonNode;

public class UpbitAutoTrader {
    public static void main(String[] args) {
        String accessKey = System.getenv("UPBIT_ACCESS_KEY");
        String secretKey = System.getenv("UPBIT_SECRET_KEY");

        if (accessKey == null || secretKey == null) {
            System.err.println("Please set environment variables UPBIT_ACCESS_KEY and UPBIT_SECRET_KEY.");
            return;
        }

        UpbitClient client = new UpbitClient(accessKey, secretKey);

        try {
            JsonNode balances = client.getBalances();
            System.out.println("Balances\n" + balances.toPrettyString());

            // Example: Market buy KRW 10000 worth of BTC
            String market = "KRW-BTC";
            String side = "bid";
            String volume = ""; // For ord_type=price, volume can be empty and price represents KRW amount
            String price = "10000";
            String ordType = "price"; // market order by price amount

            JsonNode orderResponse = client.placeOrder(market, side, volume, price, ordType);
            System.out.println("Order response\n" + orderResponse.toPrettyString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}