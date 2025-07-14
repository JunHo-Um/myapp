package com.upbit.bot.model;

import com.google.gson.annotations.SerializedName;

/**
 * 주문 요청 모델
 */
public class OrderRequest {
    @SerializedName("market")
    private String market;
    
    @SerializedName("side")
    private String side;
    
    @SerializedName("volume")
    private String volume;
    
    @SerializedName("price")
    private String price;
    
    @SerializedName("ord_type")
    private String ordType;
    
    public OrderRequest() {}
    
    public OrderRequest(String market, String side, String volume, String price, String ordType) {
        this.market = market;
        this.side = side;
        this.volume = volume;
        this.price = price;
        this.ordType = ordType;
    }
    
    public String getMarket() {
        return market;
    }
    
    public void setMarket(String market) {
        this.market = market;
    }
    
    public String getSide() {
        return side;
    }
    
    public void setSide(String side) {
        this.side = side;
    }
    
    public String getVolume() {
        return volume;
    }
    
    public void setVolume(String volume) {
        this.volume = volume;
    }
    
    public String getPrice() {
        return price;
    }
    
    public void setPrice(String price) {
        this.price = price;
    }
    
    public String getOrdType() {
        return ordType;
    }
    
    public void setOrdType(String ordType) {
        this.ordType = ordType;
    }
    
    @Override
    public String toString() {
        return "OrderRequest{" +
                "market='" + market + '\'' +
                ", side='" + side + '\'' +
                ", volume='" + volume + '\'' +
                ", price='" + price + '\'' +
                ", ordType='" + ordType + '\'' +
                '}';
    }
}