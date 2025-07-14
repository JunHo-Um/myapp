package com.upbit.trading;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 업비트 API 클라이언트 테스트
 */
public class UpbitApiClientTest {
    
    @Test
    public void testApiKeyConfiguration() {
        UpbitApiClient client = new UpbitApiClient();
        
        // API 키가 설정되지 않은 상태에서 테스트
        assertFalse("API 키가 설정되지 않았을 때 false를 반환해야 함", 
                   client.isApiKeyConfigured());
    }
    
    @Test
    public void testGetTicker() {
        UpbitApiClient client = new UpbitApiClient();
        
        try {
            JsonNode ticker = client.getTicker("KRW-BTC");
            
            // 응답이 null이 아니어야 함
            assertNotNull("티커 응답이 null이 아니어야 함", ticker);
            
            // 필수 필드들이 존재해야 함
            assertTrue("trade_price 필드가 존재해야 함", ticker.has("trade_price"));
            assertTrue("market 필드가 존재해야 함", ticker.has("market"));
            
            // 마켓 정보가 올바르게 설정되어야 함
            assertEquals("마켓 정보가 올바르게 설정되어야 함", "KRW-BTC", 
                        ticker.get("market").asText());
            
        } catch (Exception e) {
            fail("티커 조회 중 예외가 발생하면 안 됨: " + e.getMessage());
        }
    }
}