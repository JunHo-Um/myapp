package com.upbit.trading;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 거래 실행기
 * 전략에 따라 실제 매매를 실행합니다.
 */
public class TradingExecutor {
    private static final Logger logger = LoggerFactory.getLogger(TradingExecutor.class);
    
    private final UpbitApiClient apiClient;
    private final TradingStrategy strategy;
    private final String market; // 거래할 마켓 (예: KRW-BTC)
    private final ScheduledExecutorService executor;
    private final int checkInterval; // 체크 간격 (초)
    
    private boolean isRunning = false;
    private String lastOrderUuid = null;
    
    public TradingExecutor(UpbitApiClient apiClient, TradingStrategy strategy, String market) {
        this.apiClient = apiClient;
        this.strategy = strategy;
        this.market = market;
        this.executor = Executors.newScheduledThreadPool(1);
        this.checkInterval = 30; // 30초마다 체크
    }
    
    /**
     * 자동매매 시작
     */
    public void start() {
        if (isRunning) {
            logger.warn("이미 실행 중입니다.");
            return;
        }
        
        isRunning = true;
        logger.info("자동매매 시작: {}", market);
        
        executor.scheduleAtFixedRate(this::executeTrading, 0, checkInterval, TimeUnit.SECONDS);
    }
    
    /**
     * 자동매매 중지
     */
    public void stop() {
        if (!isRunning) {
            logger.warn("실행 중이 아닙니다.");
            return;
        }
        
        isRunning = false;
        logger.info("자동매매 중지: {}", market);
        
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 거래 실행 로직
     */
    private void executeTrading() {
        try {
            if (!isRunning) {
                return;
            }
            
            logger.debug("거래 체크 시작: {}", market);
            
            // 현재가 조회
            JsonNode ticker = apiClient.getTicker(market);
            if (ticker.isArray() && ticker.size() > 0) {
                ticker = ticker.get(0);
            }
            
            // 계좌 정보 조회
            JsonNode accounts = apiClient.getAccounts();
            
            // 보유 자산 확인
            double krwBalance = getKrwBalance(accounts);
            double coinBalance = getCoinBalance(accounts, market);
            
            logger.info("현재 상태 - KRW: {}, {}: {}", krwBalance, market, coinBalance);
            
            // 매수 신호 확인
            if (strategy.shouldBuy(ticker) && krwBalance > 10000) { // 최소 1만원 이상 보유 시
                executeBuyOrder(ticker, krwBalance);
            }
            // 매도 신호 확인
            else if (strategy.shouldSell(ticker) && coinBalance > 0) {
                executeSellOrder(ticker, coinBalance);
            }
            
        } catch (Exception e) {
            logger.error("거래 실행 중 오류 발생", e);
        }
    }
    
    /**
     * 매수 주문 실행
     */
    private void executeBuyOrder(JsonNode ticker, double krwBalance) {
        try {
            double currentPrice = ticker.get("trade_price").asDouble();
            double volume = strategy.calculateBuyVolume(krwBalance, currentPrice);
            
            if (volume <= 0) {
                logger.warn("매수 수량이 0 이하입니다: {}", volume);
                return;
            }
            
            logger.info("매수 주문 실행: {} {}개 @ {}", market, volume, currentPrice);
            
            JsonNode orderResult = apiClient.placeOrder(market, "bid", volume, currentPrice, "limit");
            
            if (orderResult.has("uuid")) {
                lastOrderUuid = orderResult.get("uuid").asText();
                logger.info("매수 주문 성공: {}", lastOrderUuid);
            } else {
                logger.error("매수 주문 실패: {}", orderResult.toString());
            }
            
        } catch (Exception e) {
            logger.error("매수 주문 실행 중 오류", e);
        }
    }
    
    /**
     * 매도 주문 실행
     */
    private void executeSellOrder(JsonNode ticker, double coinBalance) {
        try {
            double currentPrice = ticker.get("trade_price").asDouble();
            double volume = strategy.calculateSellVolume(coinBalance, currentPrice);
            
            if (volume <= 0) {
                logger.warn("매도 수량이 0 이하입니다: {}", volume);
                return;
            }
            
            logger.info("매도 주문 실행: {} {}개 @ {}", market, volume, currentPrice);
            
            JsonNode orderResult = apiClient.placeOrder(market, "ask", volume, currentPrice, "limit");
            
            if (orderResult.has("uuid")) {
                lastOrderUuid = orderResult.get("uuid").asText();
                logger.info("매도 주문 성공: {}", lastOrderUuid);
            } else {
                logger.error("매도 주문 실패: {}", orderResult.toString());
            }
            
        } catch (Exception e) {
            logger.error("매도 주문 실행 중 오류", e);
        }
    }
    
    /**
     * KRW 잔고 조회
     */
    private double getKrwBalance(JsonNode accounts) {
        for (JsonNode account : accounts) {
            String currency = account.get("currency").asText();
            if ("KRW".equals(currency)) {
                return account.get("balance").asDouble();
            }
        }
        return 0.0;
    }
    
    /**
     * 코인 잔고 조회
     */
    private double getCoinBalance(JsonNode accounts, String market) {
        String currency = market.split("-")[1]; // KRW-BTC -> BTC
        
        for (JsonNode account : accounts) {
            String accountCurrency = account.get("currency").asText();
            if (currency.equals(accountCurrency)) {
                return account.get("balance").asDouble();
            }
        }
        return 0.0;
    }
    
    /**
     * 마지막 주문 상태 확인
     */
    public void checkLastOrderStatus() {
        if (lastOrderUuid == null) {
            return;
        }
        
        try {
            JsonNode orderStatus = apiClient.getOrders(lastOrderUuid);
            String state = orderStatus.get("state").asText();
            
            logger.info("주문 상태 확인: {} - {}", lastOrderUuid, state);
            
            // 주문이 완료되면 UUID 초기화
            if ("done".equals(state) || "cancel".equals(state)) {
                lastOrderUuid = null;
            }
            
        } catch (Exception e) {
            logger.error("주문 상태 확인 중 오류", e);
        }
    }
    
    /**
     * 실행 중인지 확인
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * 리소스 정리
     */
    public void close() {
        stop();
        try {
            apiClient.close();
        } catch (Exception e) {
            logger.error("API 클라이언트 종료 중 오류", e);
        }
    }
}