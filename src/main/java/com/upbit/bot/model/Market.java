package com.upbit.bot.model;

import com.google.gson.annotations.SerializedName;

/**
 * 마켓 정보 모델
 */
public class Market {
    @SerializedName("market")
    private String market;
    
    @SerializedName("korean_name")
    private String koreanName;
    
    @SerializedName("english_name")
    private String englishName;
    
    @SerializedName("market_warning")
    private String marketWarning;
    
    public String getMarket() {
        return market;
    }
    
    public void setMarket(String market) {
        this.market = market;
    }
    
    public String getKoreanName() {
        return koreanName;
    }
    
    public void setKoreanName(String koreanName) {
        this.koreanName = koreanName;
    }
    
    public String getEnglishName() {
        return englishName;
    }
    
    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }
    
    public String getMarketWarning() {
        return marketWarning;
    }
    
    public void setMarketWarning(String marketWarning) {
        this.marketWarning = marketWarning;
    }
    
    public boolean isKrwMarket() {
        return market.startsWith("KRW-");
    }
    
    public boolean isBtcMarket() {
        return market.startsWith("BTC-");
    }
    
    public boolean isUsdtMarket() {
        return market.startsWith("USDT-");
    }
    
    public String getBaseCurrency() {
        return market.split("-")[0];
    }
    
    public String getQuoteCurrency() {
        return market.split("-")[1];
    }
    
    @Override
    public String toString() {
        return "Market{" +
                "market='" + market + '\'' +
                ", koreanName='" + koreanName + '\'' +
                ", englishName='" + englishName + '\'' +
                ", marketWarning='" + marketWarning + '\'' +
                '}';
    }
}