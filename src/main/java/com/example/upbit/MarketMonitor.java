package com.example.upbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MarketMonitor {
    private static final Logger logger = LoggerFactory.getLogger(MarketMonitor.class);

    private final UpbitClient client;
    private final Strategy strategy;
    private final RiskManager riskManager;
    private final TradingConfig config;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final List<Double> priceHistory = new ArrayList<>();

    public MarketMonitor(UpbitClient client, Strategy strategy, RiskManager riskManager, TradingConfig config) {
        this.client = client;
        this.strategy = strategy;
        this.riskManager = riskManager;
        this.config = config;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::tick, 0, config.pollingSeconds, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }

    private void tick() {
        try {
            double price = client.getTickerPrice(config.market);
            priceHistory.add(price);
            logger.info("Fetched price: {}", price);

            OrderDecision decision = strategy.evaluate(priceHistory);
            if (riskManager.allow(decision)) {
                executeTrade(decision);
            } else {
                logger.debug("Trade not allowed by risk manager or decision is HOLD.");
            }

            // Keep history size reasonable
            int maxHistory = Math.max(config.longWindow * 5, 1000);
            if (priceHistory.size() > maxHistory) {
                priceHistory.remove(0);
            }
        } catch (Exception e) {
            logger.error("Error in market monitor tick", e);
        }
    }

    private void executeTrade(OrderDecision decision) {
        try {
            if (decision.getType() == OrderDecision.DecisionType.BUY) {
                client.placeOrder(config.market, "bid", "", String.valueOf(config.tradeAmountKrw), "price");
                riskManager.updateAfterTrade(true);
                logger.info("BUY order placed for {} KRW", config.tradeAmountKrw);
            } else if (decision.getType() == OrderDecision.DecisionType.SELL) {
                // For this example we sell using same KRW amount exposure approximation
                client.placeOrder(config.market, "ask", "", String.valueOf(config.tradeAmountKrw), "price");
                riskManager.updateAfterTrade(false);
                logger.info("SELL order placed for {} KRW", config.tradeAmountKrw);
            }
        } catch (Exception e) {
            logger.error("Failed to execute trade", e);
        }
    }
}