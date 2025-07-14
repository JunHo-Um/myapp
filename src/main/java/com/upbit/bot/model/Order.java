package com.upbit.bot.model;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 주문 정보 모델
 */
public class Order {
    @SerializedName("uuid")
    private String uuid;
    
    @SerializedName("side")
    private String side;
    
    @SerializedName("ord_type")
    private String ordType;
    
    @SerializedName("price")
    private String price;
    
    @SerializedName("state")
    private String state;
    
    @SerializedName("market")
    private String market;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("volume")
    private String volume;
    
    @SerializedName("remaining_volume")
    private String remainingVolume;
    
    @SerializedName("reserved_fee")
    private String reservedFee;
    
    @SerializedName("remaining_fee")
    private String remainingFee;
    
    @SerializedName("paid_fee")
    private String paidFee;
    
    @SerializedName("locked")
    private String locked;
    
    @SerializedName("executed_volume")
    private String executedVolume;
    
    @SerializedName("trades_count")
    private int tradesCount;
    
    // Getters and Setters
    public String getUuid() {
        return uuid;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    public String getSide() {
        return side;
    }
    
    public void setSide(String side) {
        this.side = side;
    }
    
    public String getOrdType() {
        return ordType;
    }
    
    public void setOrdType(String ordType) {
        this.ordType = ordType;
    }
    
    public String getPrice() {
        return price;
    }
    
    public void setPrice(String price) {
        this.price = price;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getMarket() {
        return market;
    }
    
    public void setMarket(String market) {
        this.market = market;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getVolume() {
        return volume;
    }
    
    public void setVolume(String volume) {
        this.volume = volume;
    }
    
    public String getRemainingVolume() {
        return remainingVolume;
    }
    
    public void setRemainingVolume(String remainingVolume) {
        this.remainingVolume = remainingVolume;
    }
    
    public String getReservedFee() {
        return reservedFee;
    }
    
    public void setReservedFee(String reservedFee) {
        this.reservedFee = reservedFee;
    }
    
    public String getRemainingFee() {
        return remainingFee;
    }
    
    public void setRemainingFee(String remainingFee) {
        this.remainingFee = remainingFee;
    }
    
    public String getPaidFee() {
        return paidFee;
    }
    
    public void setPaidFee(String paidFee) {
        this.paidFee = paidFee;
    }
    
    public String getLocked() {
        return locked;
    }
    
    public void setLocked(String locked) {
        this.locked = locked;
    }
    
    public String getExecutedVolume() {
        return executedVolume;
    }
    
    public void setExecutedVolume(String executedVolume) {
        this.executedVolume = executedVolume;
    }
    
    public int getTradesCount() {
        return tradesCount;
    }
    
    public void setTradesCount(int tradesCount) {
        this.tradesCount = tradesCount;
    }
    
    // Utility methods
    public double getPriceAsDouble() {
        return price != null ? Double.parseDouble(price) : 0.0;
    }
    
    public double getVolumeAsDouble() {
        return volume != null ? Double.parseDouble(volume) : 0.0;
    }
    
    public double getExecutedVolumeAsDouble() {
        return executedVolume != null ? Double.parseDouble(executedVolume) : 0.0;
    }
    
    public boolean isBuyOrder() {
        return "bid".equals(side);
    }
    
    public boolean isSellOrder() {
        return "ask".equals(side);
    }
    
    public boolean isDone() {
        return "done".equals(state);
    }
    
    public boolean isWait() {
        return "wait".equals(state);
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "uuid='" + uuid + '\'' +
                ", side='" + side + '\'' +
                ", ordType='" + ordType + '\'' +
                ", price='" + price + '\'' +
                ", state='" + state + '\'' +
                ", market='" + market + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", volume='" + volume + '\'' +
                ", remainingVolume='" + remainingVolume + '\'' +
                ", executedVolume='" + executedVolume + '\'' +
                '}';
    }
}