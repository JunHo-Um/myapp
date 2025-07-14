package com.upbit.bot.strategy;

import com.upbit.bot.model.Candle;
import com.upbit.bot.model.Ticker;
import com.upbit.bot.util.TechnicalAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * RSI(Relative Strength Index) 기반 매매 전략
 * RSI가 30 이하일 때 매수, 70 이상일 때 매도하는 전략
 */
public class RSIStrategy implements TradingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(RSIStrategy.class);
    
    private final int rsiPeriod;
    private final double oversoldLevel;
    private final double overboughtLevel;
    private final double volumeThreshold;
    
    public RSIStrategy() {
        this(14, 30.0, 70.0, 1000000.0); // 기본값
    }
    
    public RSIStrategy(int rsiPeriod, double oversoldLevel, double overboughtLevel, double volumeThreshold) {
        this.rsiPeriod = rsiPeriod;
        this.oversoldLevel = oversoldLevel;
        this.overboughtLevel = overboughtLevel;
        this.volumeThreshold = volumeThreshold;
    }
    
    @Override
    public boolean shouldBuy(List<Candle> candles, Ticker ticker) {
        if (candles == null || candles.size() < rsiPeriod + 1) {
            logger.warn("캔들 데이터가 부족합니다. 필요: {}, 현재: {}", rsiPeriod + 1, 
                       candles != null ? candles.size() : 0);
            return false;
        }
        
        try {
            // RSI 계산
            double rsi = TechnicalAnalysis.calculateRSI(candles, rsiPeriod);
            
            // 거래량 확인
            double volume24h = ticker.getAccTradePrice24h();
            
            // 매수 조건: RSI가 과매도 구간이고, 24시간 거래량이 임계값 이상
            boolean rsiCondition = rsi <= oversoldLevel;
            boolean volumeCondition = volume24h >= volumeThreshold;
            
            // 추가 조건: 최근 캔들이 상승하고 있는지 확인
            boolean priceRising = candles.get(0).getTradePrice() > candles.get(1).getTradePrice();
            
            logger.info("매수 신호 분석 - RSI: {:.2f}, 거래량(24h): {:.0f}, 가격상승: {}", 
                       rsi, volume24h, priceRising);
            
            return rsiCondition && volumeCondition && priceRising;
            
        } catch (Exception e) {
            logger.error("매수 신호 분석 중 오류 발생", e);
            return false;
        }
    }
    
    @Override
    public boolean shouldSell(List<Candle> candles, Ticker ticker) {
        if (candles == null || candles.size() < rsiPeriod + 1) {
            logger.warn("캔들 데이터가 부족합니다. 필요: {}, 현재: {}", rsiPeriod + 1, 
                       candles != null ? candles.size() : 0);
            return false;
        }
        
        try {
            // RSI 계산
            double rsi = TechnicalAnalysis.calculateRSI(candles, rsiPeriod);
            
            // 매도 조건: RSI가 과매수 구간
            boolean rsiCondition = rsi >= overboughtLevel;
            
            // 추가 조건: 최근 캔들이 하락하고 있는지 확인
            boolean priceFalling = candles.get(0).getTradePrice() < candles.get(1).getTradePrice();
            
            // 손절 조건: 현재가가 이전 대비 5% 이상 하락
            double changeRate = ticker.getSignedChangeRate();
            boolean stopLoss = changeRate <= -0.05; // -5%
            
            logger.info("매도 신호 분석 - RSI: {:.2f}, 가격하락: {}, 변화율: {:.2f}%", 
                       rsi, priceFalling, changeRate * 100);
            
            return rsiCondition || stopLoss;
            
        } catch (Exception e) {
            logger.error("매도 신호 분석 중 오류 발생", e);
            return false;
        }
    }
    
    @Override
    public String getStrategyName() {
        return "RSI Strategy";
    }
    
    @Override
    public String getDescription() {
        return String.format("RSI 기반 매매 전략 (기간: %d, 과매도: %.1f, 과매수: %.1f)", 
                           rsiPeriod, oversoldLevel, overboughtLevel);
    }
    
    // Getters
    public int getRsiPeriod() {
        return rsiPeriod;
    }
    
    public double getOversoldLevel() {
        return oversoldLevel;
    }
    
    public double getOverboughtLevel() {
        return overboughtLevel;
    }
    
    public double getVolumeThreshold() {
        return volumeThreshold;
    }
}