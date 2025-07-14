package com.upbit.trading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * 업비트 자동매매 봇 메인 클래스
 */
public class UpbitTradingBot {
    private static final Logger logger = LoggerFactory.getLogger(UpbitTradingBot.class);
    
    public static void main(String[] args) {
        logger.info("업비트 자동매매 봇 시작");
        
        try {
            // 설정 관리자 생성
            ConfigManager configManager = new ConfigManager();
            configManager.printConfig();
            
            // 설정 유효성 검사
            if (!configManager.isValid()) {
                System.err.println("⚠️  경고: API 키가 설정되지 않았습니다.");
                System.err.println("config.yaml 파일에서 API 키를 설정해주세요.");
                System.err.println("프로그램을 종료합니다.");
                return;
            }
            
            // API 클라이언트 생성
            UpbitApiClient apiClient = new UpbitApiClient();
            
            // 거래 전략 선택
            TradingStrategy strategy;
            String strategyType = configManager.getStrategyType();
            if ("bollinger_bands".equals(strategyType)) {
                strategy = new BollingerBandsStrategy();
                logger.info("볼린저 밴드 전략 사용");
            } else {
                strategy = new MovingAverageCrossoverStrategy();
                logger.info("이동평균 크로스오버 전략 사용");
            }
            
            // 거래할 마켓 설정
            String market = configManager.getMarket();
            
            // 거래 실행기 생성
            TradingExecutor executor = new TradingExecutor(apiClient, strategy, market);
            
            // 사용자 인터페이스
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("=== 업비트 자동매매 봇 ===");
            System.out.println("1. 자동매매 시작");
            System.out.println("2. 자동매매 중지");
            System.out.println("3. 계좌 정보 조회");
            System.out.println("4. 현재가 조회");
            System.out.println("5. 종료");
            System.out.println("========================");
            
            boolean running = true;
            while (running) {
                System.out.print("선택하세요 (1-5): ");
                String choice = scanner.nextLine();
                
                switch (choice) {
                    case "1":
                        if (!executor.isRunning()) {
                            executor.start();
                            System.out.println("자동매매가 시작되었습니다.");
                        } else {
                            System.out.println("이미 자동매매가 실행 중입니다.");
                        }
                        break;
                        
                    case "2":
                        if (executor.isRunning()) {
                            executor.stop();
                            System.out.println("자동매매가 중지되었습니다.");
                        } else {
                            System.out.println("자동매매가 실행 중이 아닙니다.");
                        }
                        break;
                        
                    case "3":
                        try {
                            var accounts = apiClient.getAccounts();
                            System.out.println("=== 계좌 정보 ===");
                            for (var account : accounts) {
                                String currency = account.get("currency").asText();
                                double balance = account.get("balance").asDouble();
                                double locked = account.get("locked").asDouble();
                                double avgBuyPrice = account.get("avg_buy_price").asDouble();
                                
                                if (balance > 0 || locked > 0) {
                                    System.out.printf("%s: %.8f (잠금: %.8f, 평균매수가: %.2f)\n", 
                                        currency, balance, locked, avgBuyPrice);
                                }
                            }
                        } catch (Exception e) {
                            logger.error("계좌 정보 조회 중 오류", e);
                            System.out.println("계좌 정보 조회에 실패했습니다.");
                        }
                        break;
                        
                    case "4":
                        try {
                            var ticker = apiClient.getTicker(market);
                            if (ticker.isArray() && ticker.size() > 0) {
                                var tickerData = ticker.get(0);
                                double tradePrice = tickerData.get("trade_price").asDouble();
                                double changeRate = tickerData.get("signed_change_rate").asDouble();
                                double accTradeVolume24h = tickerData.get("acc_trade_volume_24h").asDouble();
                                
                                System.out.println("=== 현재가 정보 ===");
                                System.out.printf("마켓: %s\n", market);
                                System.out.printf("현재가: %,d원\n", (int)tradePrice);
                                System.out.printf("변화율: %.2f%%\n", changeRate * 100);
                                System.out.printf("24시간 거래량: %.2f\n", accTradeVolume24h);
                            }
                        } catch (Exception e) {
                            logger.error("현재가 조회 중 오류", e);
                            System.out.println("현재가 조회에 실패했습니다.");
                        }
                        break;
                        
                    case "5":
                        running = false;
                        if (executor.isRunning()) {
                            executor.stop();
                        }
                        executor.close();
                        System.out.println("프로그램을 종료합니다.");
                        break;
                        
                    default:
                        System.out.println("잘못된 선택입니다. 1-5 중에서 선택해주세요.");
                        break;
                }
                
                System.out.println();
            }
            
            scanner.close();
            
        } catch (Exception e) {
            logger.error("프로그램 실행 중 오류 발생", e);
            System.err.println("프로그램 실행 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}