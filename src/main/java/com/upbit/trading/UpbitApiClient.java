package com.upbit.trading;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 업비트 API 클라이언트
 * 업비트 API와의 모든 통신을 담당합니다.
 */
public class UpbitApiClient {
    private static final Logger logger = LoggerFactory.getLogger(UpbitApiClient.class);
    
    private static final String BASE_URL = "https://api.upbit.com/v1";
    private static final String ACCESS_KEY = "YOUR_ACCESS_KEY"; // 실제 액세스 키로 변경 필요
    private static final String SECRET_KEY = "YOUR_SECRET_KEY"; // 실제 시크릿 키로 변경 필요
    
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public UpbitApiClient() {
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 계좌 정보 조회
     */
    public JsonNode getAccounts() throws Exception {
        String url = BASE_URL + "/accounts";
        String jwt = createJWT("GET", "/v1/accounts", null);
        
        HttpGet request = new HttpGet(url);
        request.setHeader("Authorization", "Bearer " + jwt);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            logger.info("계좌 정보 조회 응답: {}", responseBody);
            return objectMapper.readTree(responseBody);
        }
    }
    
    /**
     * 시장 코드 조회
     */
    public JsonNode getMarkets() throws Exception {
        String url = BASE_URL + "/market/all";
        
        HttpGet request = new HttpGet(url);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            logger.info("시장 코드 조회 응답: {}", responseBody);
            return objectMapper.readTree(responseBody);
        }
    }
    
    /**
     * 현재가 조회
     */
    public JsonNode getTicker(String markets) throws Exception {
        String url = BASE_URL + "/ticker?markets=" + markets;
        
        HttpGet request = new HttpGet(url);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            logger.info("현재가 조회 응답: {}", responseBody);
            return objectMapper.readTree(responseBody);
        }
    }
    
    /**
     * 주문하기
     */
    public JsonNode placeOrder(String market, String side, double volume, double price, String ordType) throws Exception {
        String url = BASE_URL + "/orders";
        
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("market", market);
        orderData.put("side", side);
        orderData.put("volume", String.valueOf(volume));
        orderData.put("price", String.valueOf(price));
        orderData.put("ord_type", ordType);
        
        String queryString = createQueryString(orderData);
        String jwt = createJWT("POST", "/v1/orders", queryString);
        
        HttpPost request = new HttpPost(url);
        request.setHeader("Authorization", "Bearer " + jwt);
        request.setHeader("Content-Type", "application/json");
        
        String jsonBody = objectMapper.writeValueAsString(orderData);
        request.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            logger.info("주문 응답: {}", responseBody);
            return objectMapper.readTree(responseBody);
        }
    }
    
    /**
     * 주문 조회
     */
    public JsonNode getOrders(String uuid) throws Exception {
        String url = BASE_URL + "/order?uuid=" + uuid;
        String jwt = createJWT("GET", "/v1/order", "uuid=" + uuid);
        
        HttpGet request = new HttpGet(url);
        request.setHeader("Authorization", "Bearer " + jwt);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            logger.info("주문 조회 응답: {}", responseBody);
            return objectMapper.readTree(responseBody);
        }
    }
    
    /**
     * 주문 취소
     */
    public JsonNode cancelOrder(String uuid) throws Exception {
        String url = BASE_URL + "/order";
        
        Map<String, Object> cancelData = new HashMap<>();
        cancelData.put("uuid", uuid);
        
        String queryString = createQueryString(cancelData);
        String jwt = createJWT("DELETE", "/v1/order", queryString);
        
        HttpPost request = new HttpPost(url);
        request.setHeader("Authorization", "Bearer " + jwt);
        request.setHeader("Content-Type", "application/json");
        
        String jsonBody = objectMapper.writeValueAsString(cancelData);
        request.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            logger.info("주문 취소 응답: {}", responseBody);
            return objectMapper.readTree(responseBody);
        }
    }
    
    /**
     * JWT 토큰 생성
     */
    private String createJWT(String method, String path, String queryString) throws Exception {
        String payload = method + " " + path;
        if (queryString != null && !queryString.isEmpty()) {
            payload += " " + queryString;
        }
        
        String nonce = UUID.randomUUID().toString();
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("access_key", ACCESS_KEY);
        claims.put("nonce", nonce);
        if (queryString != null && !queryString.isEmpty()) {
            claims.put("query_hash", sha512(queryString));
            claims.put("query_hash_alg", "SHA512");
        }
        
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .compact();
    }
    
    /**
     * SHA512 해시 생성
     */
    private String sha512(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        
        return hexString.toString();
    }
    
    /**
     * 쿼리 스트링 생성
     */
    private String createQueryString(Map<String, Object> params) {
        StringBuilder queryString = new StringBuilder();
        boolean first = true;
        
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (!first) {
                queryString.append("&");
            }
            queryString.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        
        return queryString.toString();
    }
    
    /**
     * 리소스 정리
     */
    public void close() throws Exception {
        if (httpClient != null) {
            httpClient.close();
        }
    }
}