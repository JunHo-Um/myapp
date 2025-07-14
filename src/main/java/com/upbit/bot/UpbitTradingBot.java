package com.upbit.bot;

import com.upbit.bot.api.UpbitApiClient;
import com.upbit.bot.config.TradingConfig;
import com.upbit.bot.model.*;
import com.upbit.bot.strategy.RSIStrategy;
import com.upbit.bot.strategy.TradingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 업비트 자동 매매 봇 메인 클래스
 */
public class UpbitTradingBot {
    private static final Logger logger = LoggerFactory.getLogger(UpbitTradingBot.class);
    
    private final TradingConfig config;
    private final UpbitApiClient apiClient;
    private final TradingStrategy strategy;
    private final ScheduledExecutorService scheduler;
    
    private volatile boolean isRunning = false;
    private volatile boolean hasPosition = false;
    private double totalProfit = 0.0;
    private double dailyProfit = 0.0;
    private LocalDateTime lastResetDate = LocalDateTime.now();
    
    public UpbitTradingBot(TradingConfig config) {
        this.config = config;
        this.apiClient = new UpbitApiClient(config.getAccessKey(), config.getSecretKey());
        this.strategy = new RSIStrategy(); // RSI 전략 사용
        this.scheduler = Executors.newScheduledThreadPool(2);
        
        logger.info("업비트 자동 매매 봇이 초기화되었습니다.");
        logger.info("설정: {}", config);
        logger.info("전략: {}", strategy.getDescription());
    }
    
    public static void main(String[] args) {
        logger.info("=== 업비트 자동 매매 봇 시작 ===");
        
        try {
            // 설정 생성 (실제 사용시에는 환경변수나 설정파일에서 읽어오세요)
            TradingConfig config = new TradingConfig();
            
            // 환경변수에서 API 키 읽기
            String accessKey = System.getenv("UPBIT_ACCESS_KEY");
            String secretKey = System.getenv("UPBIT_SECRET_KEY");
            
            if (accessKey == null || secretKey == null) {
                logger.warn("API 키가 설정되지 않았습니다. 모의 거래 모드로 실행됩니다.");
                logger.info("환경변수 UPBIT_ACCESS_KEY와 UPBIT_SECRET_KEY를 설정해주세요.");
                config.setDryRun(true);
            } else {
                config.setAccessKey(accessKey);
                config.setSecretKey(secretKey);
                logger.info("API 키가 설정되었습니다.");
            }
            
            // 봇 생성 및 시작
            UpbitTradingBot bot = new UpbitTradingBot(config);
            
            // 종료 훅 등록
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("봇 종료 중...");
                bot.stop();
            }));
            
            // 봇 시작
            bot.start();
            
        } catch (Exception e) {
            logger.error("봇 실행 중 오류 발생", e);
            System.exit(1);
        }
    }
    
    public void start() {
        if (isRunning) {
            logger.warn("봇이 이미 실행 중입니다.");
            return;
        }
        
        isRunning = true;
        logger.info("매매 봇을 시작합니다...");
        
        // 초기 계좌 정보 확인
        if (!config.isDryRun()) {
            checkAccountStatus();
        }
        
        // 매매 로직을 주기적으로 실행
        scheduler.scheduleAtFixedRate(
            this::executeTradingCycle,
            0,
            config.getTradingInterval(),
            TimeUnit.MILLISECONDS
        );
        
        // 일일 수익률 리셋 스케줄러
        scheduler.scheduleAtFixedRate(
            this::resetDailyProfit,
            getMillisUntilMidnight(),
            24 * 60 * 60 * 1000, // 24시간
            TimeUnit.MILLISECONDS
        );
        
        logger.info("매매 봇이 시작되었습니다. 매매 간격: {}ms", config.getTradingInterval());
    }
    
    public void stop() {
        isRunning = false;
        scheduler.shutdown();
        
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        logger.info("매매 봇이 종료되었습니다. 총 수익: {:.2f}원", totalProfit);
    }
    
    private void executeTradingCycle() {
        if (!isRunning) return;
        
        try {
            logger.debug("매매 사이클 실행 시작 - {}", 
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // 일일 손실 한도 확인
            if (dailyProfit <= config.getMaxDailyLoss() * getInitialBalance()) {
                logger.warn("일일 최대 손실 한도에 도달했습니다. 오늘의 거래를 중단합니다.");
                return;
            }
            
            // 시장 데이터 수집
            List<Candle> candles = apiClient.getMinuteCandles(
                config.getCandleInterval(), 
                config.getTargetMarket(), 
                config.getCandleCount()
            );
            
            List<Ticker> tickers = apiClient.getTicker(config.getTargetMarket());
            if (tickers.isEmpty()) {
                logger.warn("티커 정보를 가져올 수 없습니다.");
                return;
            }
            
            Ticker ticker = tickers.get(0);
            
            // 포지션 상태 확인
            updatePositionStatus();
            
            // 매매 신호 분석 및 실행
            if (!hasPosition) {
                // 매수 신호 확인
                if (strategy.shouldBuy(candles, ticker)) {
                    executeBuyOrder(ticker);
                }
            } else {
                // 매도 신호 확인
                if (strategy.shouldSell(candles, ticker)) {
                    executeSellOrder(ticker);
                }
            }
            
        } catch (Exception e) {
            logger.error("매매 사이클 실행 중 오류 발생", e);
        }
    }
    
    private void executeBuyOrder(Ticker ticker) {
        try {
            if (config.isDryRun()) {
                logger.info("📈 [모의거래] 매수 신호 - 가격: {:.0f}원", ticker.getTradePrice());
                hasPosition = true;
                return;
            }
            
            // 실제 매수 로직
            double availableBalance = getAvailableKrwBalance();
            double investmentAmount = config.calculateInvestmentAmount(availableBalance);
            
            if (investmentAmount < config.getMinInvestmentAmount()) {
                logger.warn("투자 가능 금액이 부족합니다. 필요: {:.0f}원, 보유: {:.0f}원", 
                           config.getMinInvestmentAmount(), availableBalance);
                return;
            }
            
            // 시장가 매수
            double volume = investmentAmount / ticker.getTradePrice();
            Order order = apiClient.placeOrder(
                config.getTargetMarket(),
                "bid",
                String.format("%.8f", volume),
                null,
                "price"
            );
            
            logger.info("📈 매수 주문 완료 - UUID: {}, 금액: {:.0f}원, 수량: {:.6f}", 
                       order.getUuid(), investmentAmount, volume);
            hasPosition = true;
            
        } catch (Exception e) {
            logger.error("매수 주문 실행 중 오류 발생", e);
        }
    }
    
    private void executeSellOrder(Ticker ticker) {
        try {
            if (config.isDryRun()) {
                logger.info("📉 [모의거래] 매도 신호 - 가격: {:.0f}원", ticker.getTradePrice());
                hasPosition = false;
                return;
            }
            
            // 실제 매도 로직
            double availableVolume = getAvailableCoinBalance();
            
            if (availableVolume <= 0) {
                logger.warn("매도할 코인이 없습니다.");
                hasPosition = false;
                return;
            }
            
            // 시장가 매도
            Order order = apiClient.placeOrder(
                config.getTargetMarket(),
                "ask",
                String.format("%.8f", availableVolume),
                null,
                "market"
            );
            
            double sellAmount = availableVolume * ticker.getTradePrice();
            logger.info("📉 매도 주문 완료 - UUID: {}, 금액: {:.0f}원, 수량: {:.6f}", 
                       order.getUuid(), sellAmount, availableVolume);
            
            hasPosition = false;
            updateProfit(sellAmount);
            
        } catch (Exception e) {
            logger.error("매도 주문 실행 중 오류 발생", e);
        }
    }
    
    private void updatePositionStatus() {
        if (config.isDryRun()) return;
        
        try {
            double coinBalance = getAvailableCoinBalance();
            hasPosition = coinBalance > 0.00001; // 최소 단위 고려
        } catch (Exception e) {
            logger.error("포지션 상태 업데이트 중 오류 발생", e);
        }
    }
    
    private double getAvailableKrwBalance() {
        try {
            List<Account> accounts = apiClient.getAccounts();
            return accounts.stream()
                    .filter(account -> "KRW".equals(account.getCurrency()))
                    .mapToDouble(Account::getAvailableBalance)
                    .findFirst()
                    .orElse(0.0);
        } catch (Exception e) {
            logger.error("KRW 잔고 조회 중 오류 발생", e);
            return 0.0;
        }
    }
    
    private double getAvailableCoinBalance() {
        try {
            String coin = config.getTargetMarket().split("-")[1];
            List<Account> accounts = apiClient.getAccounts();
            return accounts.stream()
                    .filter(account -> coin.equals(account.getCurrency()))
                    .mapToDouble(Account::getAvailableBalance)
                    .findFirst()
                    .orElse(0.0);
        } catch (Exception e) {
            logger.error("코인 잔고 조회 중 오류 발생", e);
            return 0.0;
        }
    }
    
    private void checkAccountStatus() {
        try {
            List<Account> accounts = apiClient.getAccounts();
            logger.info("=== 계좌 정보 ===");
            for (Account account : accounts) {
                if (account.getBalanceAsDouble() > 0) {
                    logger.info("{}: {:.6f} (가용: {:.6f})", 
                               account.getCurrency(), 
                               account.getBalanceAsDouble(),
                               account.getAvailableBalance());
                }
            }
        } catch (Exception e) {
            logger.error("계좌 정보 조회 실패", e);
        }
    }
    
    private void updateProfit(double sellAmount) {
        // 간단한 수익 계산 (실제로는 더 정교한 계산이 필요)
        // 이 예시에서는 매도 금액을 기준으로 수익을 계산
        double profit = sellAmount * 0.001; // 임시 수익률
        totalProfit += profit;
        dailyProfit += profit;
        
        logger.info("💰 거래 완료 - 수익: {:.2f}원, 총 수익: {:.2f}원, 일일 수익: {:.2f}원", 
                   profit, totalProfit, dailyProfit);
    }
    
    private void resetDailyProfit() {
        LocalDateTime now = LocalDateTime.now();
        if (now.toLocalDate().isAfter(lastResetDate.toLocalDate())) {
            logger.info("일일 수익률 리셋 - 어제 수익: {:.2f}원", dailyProfit);
            dailyProfit = 0.0;
            lastResetDate = now;
        }
    }
    
    private long getMillisUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return java.time.Duration.between(now, midnight).toMillis();
    }
    
    private double getInitialBalance() {
        // 초기 잔고를 반환 (실제로는 시작시 잔고를 저장해야 함)
        return 1000000.0; // 임시값
    }
    
    // Getters
    public boolean isRunning() {
        return isRunning;
    }
    
    public boolean hasPosition() {
        return hasPosition;
    }
    
    public double getTotalProfit() {
        return totalProfit;
    }
    
    public double getDailyProfit() {
        return dailyProfit;
    }
}