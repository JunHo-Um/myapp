package com.upbit.trading;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 자동매매 전략 클래스
 */
public class TradingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(TradingStrategy.class);
    
    private final UpbitApiClient apiClient;
    private final String market;
    private final BigDecimal buyAmount; // 매수 금액 (원)
    private final BigDecimal sellPercentage; // 매도 비율 (0.1 = 10%)
    private final BigDecimal profitTarget; // 수익 목표 (0.05 = 5%)
    private final BigDecimal stopLoss; // 손절 비율 (0.02 = 2%)
    
    private BigDecimal lastBuyPrice = BigDecimal.ZERO;
    private BigDecimal totalInvested = BigDecimal.ZERO;
    private BigDecimal totalProfit = BigDecimal.ZERO;
    
    public TradingStrategy(UpbitApiClient apiClient, String market, 
                          BigDecimal buyAmount, BigDecimal sellPercentage,
                          BigDecimal profitTarget, BigDecimal stopLoss) {
        this.apiClient = apiClient;
        this.market = market;
        this.buyAmount = buyAmount;
        this.sellPercentage = sellPercentage;
        this.profitTarget = profitTarget;
        this.stopLoss = stopLoss;
    }
    
    /**
     * 매매 전략 실행
     */
    public void executeStrategy() {
        try {
            // 현재가 조회
            JsonNode ticker = apiClient.getTicker(market);
            BigDecimal currentPrice = new BigDecimal(ticker.get("trade_price").asText());
            
            logger.info("현재가: {}원", currentPrice);
            
            // 계좌 조회
            JsonNode accounts = apiClient.getAccounts();
            BigDecimal krwBalance = getKrwBalance(accounts);
            BigDecimal coinBalance = getCoinBalance(accounts, market);
            
            logger.info("KRW 잔고: {}원, {} 잔고: {}", krwBalance, market, coinBalance);
            
            // 매수 조건 확인
            if (shouldBuy(currentPrice, krwBalance)) {
                executeBuy(currentPrice, krwBalance);
            }
            
            // 매도 조건 확인
            if (shouldSell(currentPrice, coinBalance)) {
                executeSell(currentPrice, coinBalance);
            }
            
        } catch (Exception e) {
            logger.error("전략 실행 중 오류 발생", e);
        }
    }
    
    /**
     * 매수 조건 확인
     */
    private boolean shouldBuy(BigDecimal currentPrice, BigDecimal krwBalance) {
        // 충분한 KRW가 있고, 이전 매수 가격보다 낮거나 처음 매수하는 경우
        return krwBalance.compareTo(buyAmount) >= 0 && 
               (lastBuyPrice.equals(BigDecimal.ZERO) || 
                currentPrice.compareTo(lastBuyPrice.multiply(BigDecimal.valueOf(0.98))) < 0);
    }
    
    /**
     * 매도 조건 확인
     */
    private boolean shouldSell(BigDecimal currentPrice, BigDecimal coinBalance) {
        if (lastBuyPrice.equals(BigDecimal.ZERO) || coinBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        BigDecimal profitRatio = currentPrice.subtract(lastBuyPrice)
                                           .divide(lastBuyPrice, 4, RoundingMode.HALF_UP);
        
        // 수익 목표 달성 또는 손절 조건
        return profitRatio.compareTo(profitTarget) >= 0 || 
               profitRatio.compareTo(stopLoss.negate()) <= 0;
    }
    
    /**
     * 매수 실행
     */
    private void executeBuy(BigDecimal currentPrice, BigDecimal krwBalance) {
        try {
            BigDecimal actualBuyAmount = buyAmount.min(krwBalance);
            
            logger.info("매수 실행: {}원으로 {} 매수", actualBuyAmount, market);
            
            JsonNode result = apiClient.marketBuy(market, actualBuyAmount.toString());
            
            if (result.has("uuid")) {
                lastBuyPrice = currentPrice;
                totalInvested = totalInvested.add(actualBuyAmount);
                logger.info("매수 성공: 주문 UUID: {}", result.get("uuid").asText());
            } else {
                logger.error("매수 실패: {}", result.toString());
            }
            
        } catch (Exception e) {
            logger.error("매수 실행 중 오류", e);
        }
    }
    
    /**
     * 매도 실행
     */
    private void executeSell(BigDecimal currentPrice, BigDecimal coinBalance) {
        try {
            BigDecimal sellVolume = coinBalance.multiply(sellPercentage);
            
            logger.info("매도 실행: {} {} 매도", sellVolume, market);
            
            JsonNode result = apiClient.marketSell(market, sellVolume.toString());
            
            if (result.has("uuid")) {
                BigDecimal profit = currentPrice.subtract(lastBuyPrice)
                                              .multiply(sellVolume)
                                              .divide(lastBuyPrice, 0, RoundingMode.HALF_UP);
                totalProfit = totalProfit.add(profit);
                
                logger.info("매도 성공: 주문 UUID: {}, 수익: {}원", 
                           result.get("uuid").asText(), profit);
                logger.info("총 투자금: {}원, 총 수익: {}원", totalInvested, totalProfit);
            } else {
                logger.error("매도 실패: {}", result.toString());
            }
            
        } catch (Exception e) {
            logger.error("매도 실행 중 오류", e);
        }
    }
    
    /**
     * KRW 잔고 조회
     */
    private BigDecimal getKrwBalance(JsonNode accounts) {
        for (JsonNode account : accounts) {
            if ("KRW".equals(account.get("currency").asText())) {
                return new BigDecimal(account.get("balance").asText());
            }
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * 코인 잔고 조회
     */
    private BigDecimal getCoinBalance(JsonNode accounts, String market) {
        String currency = market.split("-")[1]; // KRW-BTC -> BTC
        for (JsonNode account : accounts) {
            if (currency.equals(account.get("currency").asText())) {
                return new BigDecimal(account.get("balance").asText());
            }
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * 전략 정보 출력
     */
    public void printStrategyInfo() {
        logger.info("=== 자동매매 전략 정보 ===");
        logger.info("거래 마켓: {}", market);
        logger.info("매수 금액: {}원", buyAmount);
        logger.info("매도 비율: {}%", sellPercentage.multiply(BigDecimal.valueOf(100)));
        logger.info("수익 목표: {}%", profitTarget.multiply(BigDecimal.valueOf(100)));
        logger.info("손절 비율: {}%", stopLoss.multiply(BigDecimal.valueOf(100)));
        logger.info("총 투자금: {}원", totalInvested);
        logger.info("총 수익: {}원", totalProfit);
        logger.info("========================");
    }
}