package com.upbit.trading;

import org.yaml.snakeyaml.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 설정 파일 관리 클래스
 * YAML 설정 파일을 읽고 관리합니다.
 */
public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    
    private static final String CONFIG_FILE = "config.yaml";
    private Map<String, Object> config;
    
    public ConfigManager() {
        loadConfig();
    }
    
    /**
     * 설정 파일 로드
     */
    private void loadConfig() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(CONFIG_FILE);
            config = yaml.load(inputStream);
            logger.info("설정 파일 로드 완료: {}", CONFIG_FILE);
        } catch (IOException e) {
            logger.error("설정 파일 로드 실패", e);
            // 기본 설정 사용
            config = getDefaultConfig();
        }
    }
    
    /**
     * 기본 설정 반환
     */
    private Map<String, Object> getDefaultConfig() {
        return Map.of(
            "api", Map.of(
                "access_key", "YOUR_ACCESS_KEY",
                "secret_key", "YOUR_SECRET_KEY"
            ),
            "trading", Map.of(
                "market", "KRW-BTC",
                "check_interval", 30,
                "min_krw_balance", 10000
            ),
            "strategy", Map.of(
                "type", "moving_average"
            )
        );
    }
    
    /**
     * API 액세스 키 조회
     */
    public String getAccessKey() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> apiConfig = (Map<String, Object>) config.get("api");
            return (String) apiConfig.get("access_key");
        } catch (Exception e) {
            logger.error("액세스 키 조회 실패", e);
            return "YOUR_ACCESS_KEY";
        }
    }
    
    /**
     * API 시크릿 키 조회
     */
    public String getSecretKey() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> apiConfig = (Map<String, Object>) config.get("api");
            return (String) apiConfig.get("secret_key");
        } catch (Exception e) {
            logger.error("시크릿 키 조회 실패", e);
            return "YOUR_SECRET_KEY";
        }
    }
    
    /**
     * 거래 마켓 조회
     */
    public String getMarket() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> tradingConfig = (Map<String, Object>) config.get("trading");
            return (String) tradingConfig.get("market");
        } catch (Exception e) {
            logger.error("거래 마켓 조회 실패", e);
            return "KRW-BTC";
        }
    }
    
    /**
     * 체크 간격 조회 (초)
     */
    public int getCheckInterval() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> tradingConfig = (Map<String, Object>) config.get("trading");
            return (Integer) tradingConfig.get("check_interval");
        } catch (Exception e) {
            logger.error("체크 간격 조회 실패", e);
            return 30;
        }
    }
    
    /**
     * 최소 KRW 잔고 조회
     */
    public int getMinKrwBalance() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> tradingConfig = (Map<String, Object>) config.get("trading");
            return (Integer) tradingConfig.get("min_krw_balance");
        } catch (Exception e) {
            logger.error("최소 KRW 잔고 조회 실패", e);
            return 10000;
        }
    }
    
    /**
     * 전략 타입 조회
     */
    public String getStrategyType() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> strategyConfig = (Map<String, Object>) config.get("strategy");
            return (String) strategyConfig.get("type");
        } catch (Exception e) {
            logger.error("전략 타입 조회 실패", e);
            return "moving_average";
        }
    }
    
    /**
     * 설정 유효성 검사
     */
    public boolean isValid() {
        String accessKey = getAccessKey();
        String secretKey = getSecretKey();
        
        return !"YOUR_ACCESS_KEY".equals(accessKey) && 
               !"YOUR_SECRET_KEY".equals(secretKey) &&
               !accessKey.isEmpty() && 
               !secretKey.isEmpty();
    }
    
    /**
     * 설정 정보 출력
     */
    public void printConfig() {
        logger.info("=== 설정 정보 ===");
        logger.info("거래 마켓: {}", getMarket());
        logger.info("체크 간격: {}초", getCheckInterval());
        logger.info("최소 KRW 잔고: {}원", getMinKrwBalance());
        logger.info("전략 타입: {}", getStrategyType());
        logger.info("API 키 설정: {}", isValid() ? "완료" : "미완료");
        logger.info("================");
    }
}