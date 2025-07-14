package com.example.upbit;

public class RiskManager {
    private final TradingConfig config;
    private double cumulativeLossKrw = 0;
    private double currentExposureKrw = 0;

    public RiskManager(TradingConfig config) {
        this.config = config;
    }

    public boolean allow(OrderDecision decision) {
        if (decision.getType() == OrderDecision.DecisionType.HOLD) {
            return false;
        }
        if (cumulativeLossKrw >= config.maxDailyLossKrw) {
            return false;
        }
        if (decision.getType() == OrderDecision.DecisionType.BUY &&
                currentExposureKrw + config.tradeAmountKrw > config.maxPositionExposureKrw) {
            return false;
        }
        return true;
    }

    public void updateAfterTrade(boolean isBuy) {
        if (isBuy) {
            currentExposureKrw += config.tradeAmountKrw;
        } else {
            currentExposureKrw = Math.max(0, currentExposureKrw - config.tradeAmountKrw);
        }
    }

    public void registerLoss(double lossKrw) {
        cumulativeLossKrw += lossKrw;
    }
}