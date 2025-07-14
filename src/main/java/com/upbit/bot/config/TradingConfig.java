package com.upbit.bot.config;

/**
 * 매매 봇 설정 클래스
 */
public class TradingConfig {
    // API 설정
    private String accessKey;
    private String secretKey;
    
    // 매매 설정
    private String targetMarket = "KRW-BTC"; // 기본 거래 마켓
    private double investmentRatio = 0.1; // 투자 비율 (전체 자금의 10%)
    private double minInvestmentAmount = 5000.0; // 최소 투자 금액 (5,000원)
    private double maxInvestmentAmount = 1000000.0; // 최대 투자 금액 (100만원)
    
    // 전략 설정
    private int candleInterval = 5; // 캔들 간격 (분)
    private int candleCount = 100; // 조회할 캔들 개수
    private long tradingInterval = 60000; // 매매 간격 (밀리초, 기본 1분)
    
    // 리스크 관리
    private double stopLossPercentage = -0.05; // 손절 비율 (-5%)
    private double takeProfitPercentage = 0.10; // 익절 비율 (10%)
    private double maxDailyLoss = -0.20; // 일일 최대 손실률 (-20%)
    
    // 시스템 설정
    private boolean dryRun = true; // 모의 거래 모드
    private boolean logTrades = true; // 거래 로그 기록
    private String logLevel = "INFO"; // 로그 레벨
    
    // 생성자
    public TradingConfig() {}
    
    public TradingConfig(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }
    
    // Getters and Setters
    public String getAccessKey() {
        return accessKey;
    }
    
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }
    
    public String getSecretKey() {
        return secretKey;
    }
    
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
    
    public String getTargetMarket() {
        return targetMarket;
    }
    
    public void setTargetMarket(String targetMarket) {
        this.targetMarket = targetMarket;
    }
    
    public double getInvestmentRatio() {
        return investmentRatio;
    }
    
    public void setInvestmentRatio(double investmentRatio) {
        this.investmentRatio = investmentRatio;
    }
    
    public double getMinInvestmentAmount() {
        return minInvestmentAmount;
    }
    
    public void setMinInvestmentAmount(double minInvestmentAmount) {
        this.minInvestmentAmount = minInvestmentAmount;
    }
    
    public double getMaxInvestmentAmount() {
        return maxInvestmentAmount;
    }
    
    public void setMaxInvestmentAmount(double maxInvestmentAmount) {
        this.maxInvestmentAmount = maxInvestmentAmount;
    }
    
    public int getCandleInterval() {
        return candleInterval;
    }
    
    public void setCandleInterval(int candleInterval) {
        this.candleInterval = candleInterval;
    }
    
    public int getCandleCount() {
        return candleCount;
    }
    
    public void setCandleCount(int candleCount) {
        this.candleCount = candleCount;
    }
    
    public long getTradingInterval() {
        return tradingInterval;
    }
    
    public void setTradingInterval(long tradingInterval) {
        this.tradingInterval = tradingInterval;
    }
    
    public double getStopLossPercentage() {
        return stopLossPercentage;
    }
    
    public void setStopLossPercentage(double stopLossPercentage) {
        this.stopLossPercentage = stopLossPercentage;
    }
    
    public double getTakeProfitPercentage() {
        return takeProfitPercentage;
    }
    
    public void setTakeProfitPercentage(double takeProfitPercentage) {
        this.takeProfitPercentage = takeProfitPercentage;
    }
    
    public double getMaxDailyLoss() {
        return maxDailyLoss;
    }
    
    public void setMaxDailyLoss(double maxDailyLoss) {
        this.maxDailyLoss = maxDailyLoss;
    }
    
    public boolean isDryRun() {
        return dryRun;
    }
    
    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }
    
    public boolean isLogTrades() {
        return logTrades;
    }
    
    public void setLogTrades(boolean logTrades) {
        this.logTrades = logTrades;
    }
    
    public String getLogLevel() {
        return logLevel;
    }
    
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
    
    // 유틸리티 메서드
    public double calculateInvestmentAmount(double availableBalance) {
        double amount = availableBalance * investmentRatio;
        return Math.max(minInvestmentAmount, Math.min(maxInvestmentAmount, amount));
    }
    
    public boolean isValidApiKeys() {
        return accessKey != null && !accessKey.trim().isEmpty() &&
               secretKey != null && !secretKey.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "TradingConfig{" +
                "targetMarket='" + targetMarket + '\'' +
                ", investmentRatio=" + investmentRatio +
                ", minInvestmentAmount=" + minInvestmentAmount +
                ", maxInvestmentAmount=" + maxInvestmentAmount +
                ", candleInterval=" + candleInterval +
                ", candleCount=" + candleCount +
                ", tradingInterval=" + tradingInterval +
                ", stopLossPercentage=" + stopLossPercentage +
                ", takeProfitPercentage=" + takeProfitPercentage +
                ", maxDailyLoss=" + maxDailyLoss +
                ", dryRun=" + dryRun +
                ", logTrades=" + logTrades +
                ", logLevel='" + logLevel + '\'' +
                '}';
    }
}