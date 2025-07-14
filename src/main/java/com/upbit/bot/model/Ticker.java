package com.upbit.bot.model;

import com.google.gson.annotations.SerializedName;

/**
 * 현재가 정보 모델
 */
public class Ticker {
    @SerializedName("market")
    private String market;
    
    @SerializedName("trade_date")
    private String tradeDate;
    
    @SerializedName("trade_time")
    private String tradeTime;
    
    @SerializedName("trade_date_kst")
    private String tradeDateKst;
    
    @SerializedName("trade_time_kst")
    private String tradeTimeKst;
    
    @SerializedName("trade_timestamp")
    private long tradeTimestamp;
    
    @SerializedName("opening_price")
    private double openingPrice;
    
    @SerializedName("high_price")
    private double highPrice;
    
    @SerializedName("low_price")
    private double lowPrice;
    
    @SerializedName("trade_price")
    private double tradePrice;
    
    @SerializedName("prev_closing_price")
    private double prevClosingPrice;
    
    @SerializedName("change")
    private String change;
    
    @SerializedName("change_price")
    private double changePrice;
    
    @SerializedName("change_rate")
    private double changeRate;
    
    @SerializedName("signed_change_price")
    private double signedChangePrice;
    
    @SerializedName("signed_change_rate")
    private double signedChangeRate;
    
    @SerializedName("trade_volume")
    private double tradeVolume;
    
    @SerializedName("acc_trade_price")
    private double accTradePrice;
    
    @SerializedName("acc_trade_price_24h")
    private double accTradePrice24h;
    
    @SerializedName("acc_trade_volume")
    private double accTradeVolume;
    
    @SerializedName("acc_trade_volume_24h")
    private double accTradeVolume24h;
    
    @SerializedName("highest_52_week_price")
    private double highest52WeekPrice;
    
    @SerializedName("highest_52_week_date")
    private String highest52WeekDate;
    
    @SerializedName("lowest_52_week_price")
    private double lowest52WeekPrice;
    
    @SerializedName("lowest_52_week_date")
    private String lowest52WeekDate;
    
    @SerializedName("timestamp")
    private long timestamp;
    
    // Getters and Setters
    public String getMarket() {
        return market;
    }
    
    public void setMarket(String market) {
        this.market = market;
    }
    
    public String getTradeDate() {
        return tradeDate;
    }
    
    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }
    
    public String getTradeTime() {
        return tradeTime;
    }
    
    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }
    
    public String getTradeDateKst() {
        return tradeDateKst;
    }
    
    public void setTradeDateKst(String tradeDateKst) {
        this.tradeDateKst = tradeDateKst;
    }
    
    public String getTradeTimeKst() {
        return tradeTimeKst;
    }
    
    public void setTradeTimeKst(String tradeTimeKst) {
        this.tradeTimeKst = tradeTimeKst;
    }
    
    public long getTradeTimestamp() {
        return tradeTimestamp;
    }
    
    public void setTradeTimestamp(long tradeTimestamp) {
        this.tradeTimestamp = tradeTimestamp;
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
    
    public double getPrevClosingPrice() {
        return prevClosingPrice;
    }
    
    public void setPrevClosingPrice(double prevClosingPrice) {
        this.prevClosingPrice = prevClosingPrice;
    }
    
    public String getChange() {
        return change;
    }
    
    public void setChange(String change) {
        this.change = change;
    }
    
    public double getChangePrice() {
        return changePrice;
    }
    
    public void setChangePrice(double changePrice) {
        this.changePrice = changePrice;
    }
    
    public double getChangeRate() {
        return changeRate;
    }
    
    public void setChangeRate(double changeRate) {
        this.changeRate = changeRate;
    }
    
    public double getSignedChangePrice() {
        return signedChangePrice;
    }
    
    public void setSignedChangePrice(double signedChangePrice) {
        this.signedChangePrice = signedChangePrice;
    }
    
    public double getSignedChangeRate() {
        return signedChangeRate;
    }
    
    public void setSignedChangeRate(double signedChangeRate) {
        this.signedChangeRate = signedChangeRate;
    }
    
    public double getTradeVolume() {
        return tradeVolume;
    }
    
    public void setTradeVolume(double tradeVolume) {
        this.tradeVolume = tradeVolume;
    }
    
    public double getAccTradePrice() {
        return accTradePrice;
    }
    
    public void setAccTradePrice(double accTradePrice) {
        this.accTradePrice = accTradePrice;
    }
    
    public double getAccTradePrice24h() {
        return accTradePrice24h;
    }
    
    public void setAccTradePrice24h(double accTradePrice24h) {
        this.accTradePrice24h = accTradePrice24h;
    }
    
    public double getAccTradeVolume() {
        return accTradeVolume;
    }
    
    public void setAccTradeVolume(double accTradeVolume) {
        this.accTradeVolume = accTradeVolume;
    }
    
    public double getAccTradeVolume24h() {
        return accTradeVolume24h;
    }
    
    public void setAccTradeVolume24h(double accTradeVolume24h) {
        this.accTradeVolume24h = accTradeVolume24h;
    }
    
    public double getHighest52WeekPrice() {
        return highest52WeekPrice;
    }
    
    public void setHighest52WeekPrice(double highest52WeekPrice) {
        this.highest52WeekPrice = highest52WeekPrice;
    }
    
    public String getHighest52WeekDate() {
        return highest52WeekDate;
    }
    
    public void setHighest52WeekDate(String highest52WeekDate) {
        this.highest52WeekDate = highest52WeekDate;
    }
    
    public double getLowest52WeekPrice() {
        return lowest52WeekPrice;
    }
    
    public void setLowest52WeekPrice(double lowest52WeekPrice) {
        this.lowest52WeekPrice = lowest52WeekPrice;
    }
    
    public String getLowest52WeekDate() {
        return lowest52WeekDate;
    }
    
    public void setLowest52WeekDate(String lowest52WeekDate) {
        this.lowest52WeekDate = lowest52WeekDate;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    // Utility methods
    public boolean isRising() {
        return "RISE".equals(change);
    }
    
    public boolean isFalling() {
        return "FALL".equals(change);
    }
    
    public boolean isEven() {
        return "EVEN".equals(change);
    }
    
    @Override
    public String toString() {
        return "Ticker{" +
                "market='" + market + '\'' +
                ", tradePrice=" + tradePrice +
                ", changeRate=" + changeRate +
                ", accTradePrice24h=" + accTradePrice24h +
                '}';
    }
}