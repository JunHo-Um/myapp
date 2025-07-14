package com.example.upbit;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpbitClient {
    private static final String BASE_URL = "https://api.upbit.com";

    private final String accessKey;
    private final String secretKey;
    private final OkHttpClient httpClient;
    private final ObjectMapper mapper;

    public UpbitClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.httpClient = new OkHttpClient();
        this.mapper = new ObjectMapper();
    }

    private String sha512(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String authorizationToken(String query) {
        String nonce = UUID.randomUUID().toString();
        String queryHash = sha512(query);

        return "Bearer " + Jwts.builder()
                .claim("access_key", accessKey)
                .claim("nonce", nonce)
                .claim("query_hash", queryHash)
                .claim("query_hash_alg", "SHA512")
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public JsonNode getBalances() throws IOException {
        String endpoint = "/v1/accounts";
        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .addHeader("Authorization", authorizationToken(""))
                .get()
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("GET " + endpoint + " failed: " + response.code() + " " + response.message());
            }
            return mapper.readTree(response.body().string());
        }
    }

    public JsonNode placeOrder(String market, String side, String volume, String price, String ordType) throws IOException {
        String endpoint = "/v1/orders";
        String queryString = "market=" + market +
                "&side=" + side +
                "&volume=" + volume +
                "&price=" + price +
                "&ord_type=" + ordType;

        RequestBody formBody = new FormBody.Builder()
                .add("market", market)
                .add("side", side)
                .add("volume", volume)
                .add("price", price)
                .add("ord_type", ordType)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .addHeader("Authorization", authorizationToken(queryString))
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("POST " + endpoint + " failed: " + response.code() + " " + response.message());
            }
            return mapper.readTree(response.body().string());
        }
    }

    public double getTickerPrice(String market) throws IOException {
        String endpoint = "/v1/ticker?markets=" + market;
        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .get()
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("GET " + endpoint + " failed: " + response.code() + " " + response.message());
            }
            JsonNode node = mapper.readTree(response.body().string());
            return node.get(0).get("trade_price").asDouble();
        }
    }
}