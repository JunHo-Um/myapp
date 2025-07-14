package com.upbit.trading;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 거래 전략 인터페이스
 */
public interface TradingStrategy {
    
    /**
     * 매수 신호인지 확인
     * @param ticker 현재가 정보
     * @return 매수 신호 여부
     */
    boolean shouldBuy(JsonNode ticker);
    
    /**
     * 매도 신호인지 확인
     * @param ticker 현재가 정보
     * @return 매도 신호 여부
     */
    boolean shouldSell(JsonNode ticker);
    
    /**
     * 매수 수량 계산
     * @param balance 보유 잔고
     * @param currentPrice 현재가
     * @return 매수 수량
     */
    double calculateBuyVolume(double balance, double currentPrice);
    
    /**
     * 매도 수량 계산
     * @param holdings 보유 수량
     * @param currentPrice 현재가
     * @return 매도 수량
     */
    double calculateSellVolume(double holdings, double currentPrice);
}

/**
 * 이동평균 크로스오버 전략
 * 단순한 이동평균선 교차를 이용한 매매 전략
 */
class MovingAverageCrossoverStrategy implements TradingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(MovingAverageCrossoverStrategy.class);
    
    private final int shortPeriod; // 단기 이동평균 기간
    private final int longPeriod;  // 장기 이동평균 기간
    private final double buyThreshold; // 매수 임계값
    private final double sellThreshold; // 매도 임계값
    private final double maxInvestmentRatio; // 최대 투자 비율
    
    public MovingAverageCrossoverStrategy() {
        this.shortPeriod = 5;
        this.longPeriod = 20;
        this.buyThreshold = 0.02; // 2% 상승 시 매수
        this.sellThreshold = -0.02; // 2% 하락 시 매도
        this.maxInvestmentRatio = 0.1; // 보유 자금의 최대 10% 투자
    }
    
    @Override
    public boolean shouldBuy(JsonNode ticker) {
        try {
            double currentPrice = ticker.get("trade_price").asDouble();
            double changeRate = ticker.get("signed_change_rate").asDouble();
            
            // 가격 상승률이 임계값을 넘으면 매수
            if (changeRate > buyThreshold) {
                logger.info("매수 신호: 현재가 {}, 변화율 {}", currentPrice, changeRate);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            logger.error("매수 신호 확인 중 오류", e);
            return false;
        }
    }
    
    @Override
    public boolean shouldSell(JsonNode ticker) {
        try {
            double currentPrice = ticker.get("trade_price").asDouble();
            double changeRate = ticker.get("signed_change_rate").asDouble();
            
            // 가격 하락률이 임계값을 넘으면 매도
            if (changeRate < sellThreshold) {
                logger.info("매도 신호: 현재가 {}, 변화율 {}", currentPrice, changeRate);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            logger.error("매도 신호 확인 중 오류", e);
            return false;
        }
    }
    
    @Override
    public double calculateBuyVolume(double balance, double currentPrice) {
        // 보유 자금의 최대 10%로 매수
        double maxInvestment = balance * maxInvestmentRatio;
        double volume = maxInvestment / currentPrice;
        
        // 소수점 8자리까지 반올림 (업비트 최소 단위)
        volume = Math.round(volume * 100000000.0) / 100000000.0;
        
        logger.info("매수 수량 계산: 잔고 {}, 현재가 {}, 매수수량 {}", balance, currentPrice, volume);
        return volume;
    }
    
    @Override
    public double calculateSellVolume(double holdings, double currentPrice) {
        // 보유 수량의 50% 매도
        double volume = holdings * 0.5;
        
        // 소수점 8자리까지 반올림
        volume = Math.round(volume * 100000000.0) / 100000000.0;
        
        logger.info("매도 수량 계산: 보유수량 {}, 현재가 {}, 매도수량 {}", holdings, currentPrice, volume);
        return volume;
    }
}

/**
 * 볼린저 밴드 전략
 * 볼린저 밴드를 이용한 매매 전략
 */
class BollingerBandsStrategy implements TradingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(BollingerBandsStrategy.class);
    
    private final int period; // 이동평균 기간
    private final double standardDeviation; // 표준편차 배수
    private final double maxInvestmentRatio; // 최대 투자 비율
    
    public BollingerBandsStrategy() {
        this.period = 20;
        this.standardDeviation = 2.0;
        this.maxInvestmentRatio = 0.1;
    }
    
    @Override
    public boolean shouldBuy(JsonNode ticker) {
        try {
            double currentPrice = ticker.get("trade_price").asDouble();
            double changeRate = ticker.get("signed_change_rate").asDouble();
            
            // 급격한 하락 후 반등 시 매수 (볼린저 밴드 하단 터치 후 반등)
            if (changeRate < -0.05 && changeRate > -0.10) {
                logger.info("볼린저 밴드 매수 신호: 현재가 {}, 변화율 {}", currentPrice, changeRate);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            logger.error("볼린저 밴드 매수 신호 확인 중 오류", e);
            return false;
        }
    }
    
    @Override
    public boolean shouldSell(JsonNode ticker) {
        try {
            double currentPrice = ticker.get("trade_price").asDouble();
            double changeRate = ticker.get("signed_change_rate").asDouble();
            
            // 급격한 상승 후 하락 시 매도 (볼린저 밴드 상단 터치 후 하락)
            if (changeRate > 0.05 && changeRate < 0.10) {
                logger.info("볼린저 밴드 매도 신호: 현재가 {}, 변화율 {}", currentPrice, changeRate);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            logger.error("볼린저 밴드 매도 신호 확인 중 오류", e);
            return false;
        }
    }
    
    @Override
    public double calculateBuyVolume(double balance, double currentPrice) {
        double maxInvestment = balance * maxInvestmentRatio;
        double volume = maxInvestment / currentPrice;
        volume = Math.round(volume * 100000000.0) / 100000000.0;
        
        logger.info("볼린저 밴드 매수 수량: 잔고 {}, 현재가 {}, 매수수량 {}", balance, currentPrice, volume);
        return volume;
    }
    
    @Override
    public double calculateSellVolume(double holdings, double currentPrice) {
        double volume = holdings * 0.5;
        volume = Math.round(volume * 100000000.0) / 100000000.0;
        
        logger.info("볼린저 밴드 매도 수량: 보유수량 {}, 현재가 {}, 매도수량 {}", holdings, currentPrice, volume);
        return volume;
    }
}