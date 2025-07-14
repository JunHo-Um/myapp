package com.upbit.trading;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
 */
public class UpbitApiClient {
    private static final Logger logger = LoggerFactory.getLogger(UpbitApiClient.class);
    
    private static final String BASE_URL = "https://api.upbit.com/v1";
    private static final String ACCESS_KEY = System.getenv("UPBIT_ACCESS_KEY");
    private static final String SECRET_KEY = System.getenv("UPBIT_SECRET_KEY");
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public UpbitApiClient() {
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 계좌 조회
     */
    public JsonNode getAccounts() throws Exception {
        String url = BASE_URL + "/accounts";
        String jwt = generateJWT("GET", "/v1/accounts", null);
        
        HttpGet request = new HttpGet(url);
        request.setHeader("Authorization", "Bearer " + jwt);
        
        HttpResponse response = httpClient.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        
        logger.info("계좌 조회 응답: {}", responseBody);
        return objectMapper.readTree(responseBody);
    }
    
    /**
     * 시장가 매수
     */
    public JsonNode marketBuy(String market, String price) throws Exception {
        String url = BASE_URL + "/orders";
        
        Map<String, String> params = new HashMap<>();
        params.put("market", market);
        params.put("side", "bid");
        params.put("price", price);
        params.put("ord_type", "price");
        
        String queryString = buildQueryString(params);
        String jwt = generateJWT("POST", "/v1/orders", queryString);
        
        HttpPost request = new HttpPost(url);
        request.setHeader("Authorization", "Bearer " + jwt);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(objectMapper.writeValueAsString(params)));
        
        HttpResponse response = httpClient.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        
        logger.info("시장가 매수 응답: {}", responseBody);
        return objectMapper.readTree(responseBody);
    }
    
    /**
     * 시장가 매도
     */
    public JsonNode marketSell(String market, String volume) throws Exception {
        String url = BASE_URL + "/orders";
        
        Map<String, String> params = new HashMap<>();
        params.put("market", market);
        params.put("side", "ask");
        params.put("volume", volume);
        params.put("ord_type", "market");
        
        String queryString = buildQueryString(params);
        String jwt = generateJWT("POST", "/v1/orders", queryString);
        
        HttpPost request = new HttpPost(url);
        request.setHeader("Authorization", "Bearer " + jwt);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(objectMapper.writeValueAsString(params)));
        
        HttpResponse response = httpClient.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        
        logger.info("시장가 매도 응답: {}", responseBody);
        return objectMapper.readTree(responseBody);
    }
    
    /**
     * 현재가 조회
     */
    public JsonNode getTicker(String market) throws Exception {
        String url = BASE_URL + "/ticker?markets=" + market;
        
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        
        JsonNode tickers = objectMapper.readTree(responseBody);
        return tickers.get(0); // 첫 번째 결과 반환
    }
    
    /**
     * JWT 토큰 생성
     */
    private String generateJWT(String method, String path, String queryString) throws Exception {
        String queryHash = "";
        if (queryString != null && !queryString.isEmpty()) {
            MessageDigest md = MessageDigest.getInstance("SHA512");
            byte[] hash = md.digest(queryString.getBytes(StandardCharsets.UTF_8));
            queryHash = bytesToHex(hash);
        }
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("access_key", ACCESS_KEY);
        claims.put("nonce", UUID.randomUUID().toString());
        if (!queryHash.isEmpty()) {
            claims.put("query_hash", queryHash);
            claims.put("query_hash_alg", "SHA512");
        }
        
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .compact();
    }
    
    /**
     * 쿼리 스트링 생성
     */
    private String buildQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }
    
    /**
     * 바이트 배열을 16진수 문자열로 변환
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    /**
     * API 키 설정 확인
     */
    public boolean isApiKeyConfigured() {
        return ACCESS_KEY != null && !ACCESS_KEY.isEmpty() && 
               SECRET_KEY != null && !SECRET_KEY.isEmpty();
    }
}