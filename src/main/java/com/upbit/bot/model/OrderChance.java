package com.upbit.bot.model;

import com.google.gson.annotations.SerializedName;

/**
 * 주문 가능 정보 모델
 */
public class OrderChance {
    @SerializedName("bid_fee")
    private String bidFee;
    
    @SerializedName("ask_fee")
    private String askFee;
    
    @SerializedName("market")
    private Market market;
    
    @SerializedName("bid_account")
    private Account bidAccount;
    
    @SerializedName("ask_account")
    private Account askAccount;
    
    public String getBidFee() {
        return bidFee;
    }
    
    public void setBidFee(String bidFee) {
        this.bidFee = bidFee;
    }
    
    public String getAskFee() {
        return askFee;
    }
    
    public void setAskFee(String askFee) {
        this.askFee = askFee;
    }
    
    public Market getMarket() {
        return market;
    }
    
    public void setMarket(Market market) {
        this.market = market;
    }
    
    public Account getBidAccount() {
        return bidAccount;
    }
    
    public void setBidAccount(Account bidAccount) {
        this.bidAccount = bidAccount;
    }
    
    public Account getAskAccount() {
        return askAccount;
    }
    
    public void setAskAccount(Account askAccount) {
        this.askAccount = askAccount;
    }
    
    public double getBidFeeAsDouble() {
        return Double.parseDouble(bidFee);
    }
    
    public double getAskFeeAsDouble() {
        return Double.parseDouble(askFee);
    }
    
    @Override
    public String toString() {
        return "OrderChance{" +
                "bidFee='" + bidFee + '\'' +
                ", askFee='" + askFee + '\'' +
                ", market=" + market +
                ", bidAccount=" + bidAccount +
                ", askAccount=" + askAccount +
                '}';
    }
}