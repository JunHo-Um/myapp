package com.upbit.bot.model;

import com.google.gson.annotations.SerializedName;

/**
 * 계좌 정보 모델
 */
public class Account {
    @SerializedName("currency")
    private String currency;
    
    @SerializedName("balance")
    private String balance;
    
    @SerializedName("locked")
    private String locked;
    
    @SerializedName("avg_buy_price")
    private String avgBuyPrice;
    
    @SerializedName("avg_buy_price_modified")
    private boolean avgBuyPriceModified;
    
    @SerializedName("unit_currency")
    private String unitCurrency;
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getBalance() {
        return balance;
    }
    
    public void setBalance(String balance) {
        this.balance = balance;
    }
    
    public String getLocked() {
        return locked;
    }
    
    public void setLocked(String locked) {
        this.locked = locked;
    }
    
    public String getAvgBuyPrice() {
        return avgBuyPrice;
    }
    
    public void setAvgBuyPrice(String avgBuyPrice) {
        this.avgBuyPrice = avgBuyPrice;
    }
    
    public boolean isAvgBuyPriceModified() {
        return avgBuyPriceModified;
    }
    
    public void setAvgBuyPriceModified(boolean avgBuyPriceModified) {
        this.avgBuyPriceModified = avgBuyPriceModified;
    }
    
    public String getUnitCurrency() {
        return unitCurrency;
    }
    
    public void setUnitCurrency(String unitCurrency) {
        this.unitCurrency = unitCurrency;
    }
    
    public double getBalanceAsDouble() {
        return Double.parseDouble(balance);
    }
    
    public double getLockedAsDouble() {
        return Double.parseDouble(locked);
    }
    
    public double getAvgBuyPriceAsDouble() {
        return Double.parseDouble(avgBuyPrice);
    }
    
    public double getAvailableBalance() {
        return getBalanceAsDouble() - getLockedAsDouble();
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "currency='" + currency + '\'' +
                ", balance='" + balance + '\'' +
                ", locked='" + locked + '\'' +
                ", avgBuyPrice='" + avgBuyPrice + '\'' +
                ", avgBuyPriceModified=" + avgBuyPriceModified +
                ", unitCurrency='" + unitCurrency + '\'' +
                '}';
    }
}