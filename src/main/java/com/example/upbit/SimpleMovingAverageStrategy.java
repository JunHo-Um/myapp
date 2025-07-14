package com.example.upbit;

import java.util.List;

public class SimpleMovingAverageStrategy implements Strategy {
    private final int shortWindow;
    private final int longWindow;

    public SimpleMovingAverageStrategy(int shortWindow, int longWindow) {
        this.shortWindow = shortWindow;
        this.longWindow = longWindow;
    }

    @Override
    public OrderDecision evaluate(List<Double> prices) {
        if (prices.size() < longWindow) {
            return OrderDecision.HOLD;
        }

        double shortAvg = average(prices.subList(prices.size() - shortWindow, prices.size()));
        double longAvg = average(prices.subList(prices.size() - longWindow, prices.size()));

        if (shortAvg > longAvg) {
            return new OrderDecision(OrderDecision.DecisionType.BUY);
        } else if (shortAvg < longAvg) {
            return new OrderDecision(OrderDecision.DecisionType.SELL);
        }
        return OrderDecision.HOLD;
    }

    private double average(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
}