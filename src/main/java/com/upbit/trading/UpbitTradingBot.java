package com.upbit.trading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 업비트 자동매매 봇 메인 클래스
 */
public class UpbitTradingBot {
    private static final Logger logger = LoggerFactory.getLogger(UpbitTradingBot.class);
    
    public static void main(String[] args) {
        logger.info("업비트 자동매매 봇을 시작합니다...");
        
        // API 클라이언트 생성
        UpbitApiClient apiClient = new UpbitApiClient();
        
        // API 키 설정 확인
        if (!apiClient.isApiKeyConfigured()) {
            logger.error("업비트 API 키가 설정되지 않았습니다.");
            logger.error("환경변수를 설정해주세요:");
            logger.error("export UPBIT_ACCESS_KEY=your_access_key");
            logger.error("export UPBIT_SECRET_KEY=your_secret_key");
            System.exit(1);
        }
        
        // 설정 입력 받기
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== 업비트 자동매매 봇 설정 ===");
        
        System.out.print("거래할 마켓을 입력하세요 (예: KRW-BTC): ");
        String market = scanner.nextLine().toUpperCase();
        
        System.out.print("매수 금액을 입력하세요 (원): ");
        BigDecimal buyAmount = new BigDecimal(scanner.nextLine());
        
        System.out.print("매도 비율을 입력하세요 (0.1 = 10%): ");
        BigDecimal sellPercentage = new BigDecimal(scanner.nextLine());
        
        System.out.print("수익 목표를 입력하세요 (0.05 = 5%): ");
        BigDecimal profitTarget = new BigDecimal(scanner.nextLine());
        
        System.out.print("손절 비율을 입력하세요 (0.02 = 2%): ");
        BigDecimal stopLoss = new BigDecimal(scanner.nextLine());
        
        System.out.print("전략 실행 간격을 입력하세요 (초): ");
        int intervalSeconds = Integer.parseInt(scanner.nextLine());
        
        // 전략 생성
        TradingStrategy strategy = new TradingStrategy(
            apiClient, market, buyAmount, sellPercentage, profitTarget, stopLoss
        );
        
        // 전략 정보 출력
        strategy.printStrategyInfo();
        
        System.out.println("자동매매를 시작합니다. 중지하려면 Ctrl+C를 누르세요.");
        
        // 스케줄러 생성 및 전략 실행
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        
        scheduler.scheduleAtFixedRate(() -> {
            try {
                strategy.executeStrategy();
            } catch (Exception e) {
                logger.error("전략 실행 중 오류 발생", e);
            }
        }, 0, intervalSeconds, TimeUnit.SECONDS);
        
        // 종료 처리
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("자동매매 봇을 종료합니다...");
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }));
        
        // 메인 스레드 대기
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            logger.info("프로그램이 중단되었습니다.");
        }
    }
}