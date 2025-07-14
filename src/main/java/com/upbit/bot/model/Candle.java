package com.upbit.bot.model;

import com.google.gson.annotations.SerializedName;

/**
 * 캔들 차트 모델
 */
public class Candle {
    @SerializedName("market")
    private String market;
    
    @SerializedName("candle_date_time_utc")
    private String candleDateTimeUtc;
    
    @SerializedName("candle_date_time_kst")
    private String candleDateTimeKst;
    
    @SerializedName("opening_price")
    private double openingPrice;
    
    @SerializedName("high_price")
    private double highPrice;
    
    @SerializedName("low_price")
    private double lowPrice;
    
    @SerializedName("trade_price")
    private double tradePrice;
    
    @SerializedName("timestamp")
    private long timestamp;
    
    @SerializedName("candle_acc_trade_price")
    private double candleAccTradePrice;
    
    @SerializedName("candle_acc_trade_volume")
    private double candleAccTradeVolume;
    
    @SerializedName("unit")
    private int unit;
    
    // Getters and Setters
    public String getMarket() {
        return market;
    }
    
    public void setMarket(String market) {
        this.market = market;
    }
    
    public String getCandleDateTimeUtc() {
        return candleDateTimeUtc;
    }
    
    public void setCandleDateTimeUtc(String candleDateTimeUtc) {
        this.candleDateTimeUtc = candleDateTimeUtc;
    }
    
    public String getCandleDateTimeKst() {
        return candleDateTimeKst;
    }
    
    public void setCandleDateTimeKst(String candleDateTimeKst) {
        this.candleDateTimeKst = candleDateTimeKst;
    }
    
    public double getOpeningPrice() {
        return openingPrice;
    }
    
    public void setOpeningPrice(double openingPrice) {
        this.openingPrice = openingPrice;
    }
    
    public double getHighPrice() {
        return highPrice;
    }
    
    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }
    
    public double getLowPrice() {
        return lowPrice;
    }
    
    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }
    
    public double getTradePrice() {
        return tradePrice;
    }
    
    public void setTradePrice(double tradePrice) {
        this.tradePrice = tradePrice;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public double getCandleAccTradePrice() {
        return candleAccTradePrice;
    }
    
    public void setCandleAccTradePrice(double candleAccTradePrice) {
        this.candleAccTradePrice = candleAccTradePrice;
    }
    
    public double getCandleAccTradeVolume() {
        return candleAccTradeVolume;
    }
    
    public void setCandleAccTradeVolume(double candleAccTradeVolume) {
        this.candleAccTradeVolume = candleAccTradeVolume;
    }
    
    public int getUnit() {
        return unit;
    }
    
    public void setUnit(int unit) {
        this.unit = unit;
    }
    
    // Utility methods
    public boolean isBullish() {
        return tradePrice > openingPrice;
    }
    
    public boolean isBearish() {
        return tradePrice < openingPrice;
    }
    
    public boolean isDoji() {
        return tradePrice == openingPrice;
    }
    
    public double getBodySize() {
        return Math.abs(tradePrice - openingPrice);
    }
    
    public double getUpperShadow() {
        return highPrice - Math.max(openingPrice, tradePrice);
    }
    
    public double getLowerShadow() {
        return Math.min(openingPrice, tradePrice) - lowPrice;
    }
    
    @Override
    public String toString() {
        return "Candle{" +
                "market='" + market + '\'' +
                ", candleDateTimeKst='" + candleDateTimeKst + '\'' +
                ", openingPrice=" + openingPrice +
                ", highPrice=" + highPrice +
                ", lowPrice=" + lowPrice +
                ", tradePrice=" + tradePrice +
                ", candleAccTradeVolume=" + candleAccTradeVolume +
                '}';
    }
}