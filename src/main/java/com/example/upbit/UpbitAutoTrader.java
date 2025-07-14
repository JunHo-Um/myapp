package com.example.upbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpbitAutoTrader {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(UpbitAutoTrader.class);
        try {
            String configPath = args.length > 0 ? args[0] : "trading.yml";
            TradingConfig config = TradingConfig.load(configPath);

            String accessKey = System.getenv("UPBIT_ACCESS_KEY");
            String secretKey = System.getenv("UPBIT_SECRET_KEY");

            if (accessKey == null || secretKey == null) {
                logger.error("Please set environment variables UPBIT_ACCESS_KEY and UPBIT_SECRET_KEY.");
                return;
            }

            UpbitClient client = new UpbitClient(accessKey, secretKey);
            Strategy strategy = new SimpleMovingAverageStrategy(config.shortWindow, config.longWindow);
            RiskManager riskManager = new RiskManager(config);
            MarketMonitor monitor = new MarketMonitor(client, strategy, riskManager, config);

            monitor.start();
            logger.info("Started market monitor with config {}", configPath);

            // Keep main thread alive
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}