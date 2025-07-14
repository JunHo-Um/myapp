package com.example.upbit;

public class OrderDecision {
    public enum DecisionType { BUY, SELL, HOLD }

    private final DecisionType type;

    public OrderDecision(DecisionType type) {
        this.type = type;
    }

    public DecisionType getType() {
        return type;
    }

    public static final OrderDecision HOLD = new OrderDecision(DecisionType.HOLD);
}