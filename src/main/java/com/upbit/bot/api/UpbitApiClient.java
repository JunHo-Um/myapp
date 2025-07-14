package com.upbit.bot.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.upbit.bot.model.*;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 업비트 API 클라이언트
 * REST API를 통해 업비트 거래소와 통신하는 클래스
 */
public class UpbitApiClient {
    private static final Logger logger = LoggerFactory.getLogger(UpbitApiClient.class);
    
    private static final String BASE_URL = "https://api.upbit.com";
    private static final String QUOTATION_URL = BASE_URL + "/v1";
    private static final String EXCHANGE_URL = BASE_URL + "/v1";
    
    private final String accessKey;
    private final String secretKey;
    private final OkHttpClient httpClient;
    private final Gson gson;
    
    public UpbitApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
    }
    
    /**
     * JWT 토큰 생성
     */
    private String createJwtToken(String queryString) throws NoSuchAlgorithmException {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        
        String queryHash = null;
        if (queryString != null && !queryString.isEmpty()) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                md.update(queryString.getBytes("UTF-8"));
                queryHash = String.format("%0128x", new BigInteger(1, md.digest()));
            } catch (java.io.UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 encoding not supported", e);
            }
        }
        
        var builder = JWT.create()
            .withClaim("access_key", accessKey)
            .withClaim("nonce", UUID.randomUUID().toString());
            
        if (queryHash != null) {
            builder.withClaim("query_hash", queryHash)
                   .withClaim("query_hash_alg", "SHA512");
        }
        
        return builder.sign(algorithm);
    }
    
    /**
     * 인증이 필요한 GET 요청
     */
    private String authenticatedGet(String endpoint, String queryString) throws IOException, NoSuchAlgorithmException {
        String jwtToken = createJwtToken(queryString);
        String url = EXCHANGE_URL + endpoint;
        if (queryString != null && !queryString.isEmpty()) {
            url += "?" + queryString;
        }
        
        Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer " + jwtToken)
            .build();
            
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }
    
    /**
     * 인증이 필요한 POST 요청
     */
    private String authenticatedPost(String endpoint, String jsonBody) throws IOException, NoSuchAlgorithmException {
        String jwtToken = createJwtToken(jsonBody);
        
        RequestBody body = RequestBody.create(
            jsonBody, MediaType.parse("application/json; charset=utf-8"));
            
        Request request = new Request.Builder()
            .url(EXCHANGE_URL + endpoint)
            .addHeader("Authorization", "Bearer " + jwtToken)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build();
            
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }
    
    /**
     * 인증이 필요없는 GET 요청 (시세 정보)
     */
    private String publicGet(String endpoint, String queryString) throws IOException {
        String url = QUOTATION_URL + endpoint;
        if (queryString != null && !queryString.isEmpty()) {
            url += "?" + queryString;
        }
        
        Request request = new Request.Builder()
            .url(url)
            .build();
            
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }
    
    /**
     * 계좌 정보 조회
     */
    public List<Account> getAccounts() throws IOException, NoSuchAlgorithmException {
        String response = authenticatedGet("/accounts", null);
        return gson.fromJson(response, new TypeToken<List<Account>>(){}.getType());
    }
    
    /**
     * 주문 가능 정보 조회
     */
    public OrderChance getOrderChance(String market) throws IOException, NoSuchAlgorithmException {
        String queryString = "market=" + market;
        String response = authenticatedGet("/orders/chance", queryString);
        return gson.fromJson(response, OrderChance.class);
    }
    
    /**
     * 주문하기
     */
    public Order placeOrder(String market, String side, String volume, String price, String ordType) 
            throws IOException, NoSuchAlgorithmException {
        OrderRequest orderRequest = new OrderRequest(market, side, volume, price, ordType);
        String jsonBody = gson.toJson(orderRequest);
        String response = authenticatedPost("/orders", jsonBody);
        return gson.fromJson(response, Order.class);
    }
    
    /**
     * 주문 취소
     */
    public Order cancelOrder(String uuid) throws IOException, NoSuchAlgorithmException {
        String queryString = "uuid=" + uuid;
        String response = authenticatedDelete("/order", queryString);
        return gson.fromJson(response, Order.class);
    }
    
    /**
     * 인증이 필요한 DELETE 요청
     */
    private String authenticatedDelete(String endpoint, String queryString) throws IOException, NoSuchAlgorithmException {
        String jwtToken = createJwtToken(queryString);
        String url = EXCHANGE_URL + endpoint;
        if (queryString != null && !queryString.isEmpty()) {
            url += "?" + queryString;
        }
        
        Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer " + jwtToken)
            .delete()
            .build();
            
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }
    
    /**
     * 마켓 코드 조회
     */
    public List<Market> getMarkets() throws IOException {
        String response = publicGet("/market/all", null);
        return gson.fromJson(response, new TypeToken<List<Market>>(){}.getType());
    }
    
    /**
     * 캔들 차트 조회 (분 단위)
     */
    public List<Candle> getMinuteCandles(int unit, String market, int count) throws IOException {
        String queryString = String.format("market=%s&count=%d", market, count);
        String response = publicGet("/candles/minutes/" + unit, queryString);
        return gson.fromJson(response, new TypeToken<List<Candle>>(){}.getType());
    }
    
    /**
     * 현재가 정보 조회
     */
    public List<Ticker> getTicker(String markets) throws IOException {
        String queryString = "markets=" + markets;
        String response = publicGet("/ticker", queryString);
        return gson.fromJson(response, new TypeToken<List<Ticker>>(){}.getType());
    }
    
    /**
     * 호가 정보 조회
     */
    public List<Orderbook> getOrderbook(String markets) throws IOException {
        String queryString = "markets=" + markets;
        String response = publicGet("/orderbook", queryString);
        return gson.fromJson(response, new TypeToken<List<Orderbook>>(){}.getType());
    }
}