package com.upbit.bot.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 호가 정보 모델
 */
public class Orderbook {
    @SerializedName("market")
    private String market;
    
    @SerializedName("timestamp")
    private long timestamp;
    
    @SerializedName("total_ask_size")
    private double totalAskSize;
    
    @SerializedName("total_bid_size")
    private double totalBidSize;
    
    @SerializedName("orderbook_units")
    private List<OrderbookUnit> orderbookUnits;
    
    public String getMarket() {
        return market;
    }
    
    public void setMarket(String market) {
        this.market = market;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public double getTotalAskSize() {
        return totalAskSize;
    }
    
    public void setTotalAskSize(double totalAskSize) {
        this.totalAskSize = totalAskSize;
    }
    
    public double getTotalBidSize() {
        return totalBidSize;
    }
    
    public void setTotalBidSize(double totalBidSize) {
        this.totalBidSize = totalBidSize;
    }
    
    public List<OrderbookUnit> getOrderbookUnits() {
        return orderbookUnits;
    }
    
    public void setOrderbookUnits(List<OrderbookUnit> orderbookUnits) {
        this.orderbookUnits = orderbookUnits;
    }
    
    public OrderbookUnit getBestBid() {
        if (orderbookUnits != null && !orderbookUnits.isEmpty()) {
            return orderbookUnits.get(0);
        }
        return null;
    }
    
    public OrderbookUnit getBestAsk() {
        if (orderbookUnits != null && !orderbookUnits.isEmpty()) {
            return orderbookUnits.get(0);
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "Orderbook{" +
                "market='" + market + '\'' +
                ", timestamp=" + timestamp +
                ", totalAskSize=" + totalAskSize +
                ", totalBidSize=" + totalBidSize +
                ", orderbookUnits=" + orderbookUnits +
                '}';
    }
    
    /**
     * 호가 단위 모델
     */
    public static class OrderbookUnit {
        @SerializedName("ask_price")
        private double askPrice;
        
        @SerializedName("bid_price")
        private double bidPrice;
        
        @SerializedName("ask_size")
        private double askSize;
        
        @SerializedName("bid_size")
        private double bidSize;
        
        public double getAskPrice() {
            return askPrice;
        }
        
        public void setAskPrice(double askPrice) {
            this.askPrice = askPrice;
        }
        
        public double getBidPrice() {
            return bidPrice;
        }
        
        public void setBidPrice(double bidPrice) {
            this.bidPrice = bidPrice;
        }
        
        public double getAskSize() {
            return askSize;
        }
        
        public void setAskSize(double askSize) {
            this.askSize = askSize;
        }
        
        public double getBidSize() {
            return bidSize;
        }
        
        public void setBidSize(double bidSize) {
            this.bidSize = bidSize;
        }
        
        public double getSpread() {
            return askPrice - bidPrice;
        }
        
        public double getSpreadPercentage() {
            return (getSpread() / bidPrice) * 100;
        }
        
        @Override
        public String toString() {
            return "OrderbookUnit{" +
                    "askPrice=" + askPrice +
                    ", bidPrice=" + bidPrice +
                    ", askSize=" + askSize +
                    ", bidSize=" + bidSize +
                    '}';
        }
    }
}