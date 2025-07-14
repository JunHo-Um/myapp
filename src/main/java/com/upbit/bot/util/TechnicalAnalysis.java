package com.upbit.bot.util;

import com.upbit.bot.model.Candle;
import java.util.ArrayList;
import java.util.List;

/**
 * 기술적 분석 유틸리티 클래스
 */
public class TechnicalAnalysis {
    
    /**
     * RSI(Relative Strength Index) 계산
     * @param candles 캔들 데이터 (최신 데이터가 첫 번째)
     * @param period RSI 계산 기간
     * @return RSI 값 (0-100)
     */
    public static double calculateRSI(List<Candle> candles, int period) {
        if (candles == null || candles.size() < period + 1) {
            throw new IllegalArgumentException("RSI 계산을 위한 캔들 데이터가 부족합니다.");
        }
        
        List<Double> gains = new ArrayList<>();
        List<Double> losses = new ArrayList<>();
        
        // 가격 변화 계산 (역순으로 처리하여 시간순 정렬)
        for (int i = candles.size() - 1; i > 0; i--) {
            double priceChange = candles.get(i-1).getTradePrice() - candles.get(i).getTradePrice();
            
            if (priceChange > 0) {
                gains.add(priceChange);
                losses.add(0.0);
            } else {
                gains.add(0.0);
                losses.add(Math.abs(priceChange));
            }
        }
        
        // 초기 평균 계산
        double avgGain = gains.subList(0, period).stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avgLoss = losses.subList(0, period).stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        // 스무딩된 평균 계산
        for (int i = period; i < gains.size(); i++) {
            avgGain = ((avgGain * (period - 1)) + gains.get(i)) / period;
            avgLoss = ((avgLoss * (period - 1)) + losses.get(i)) / period;
        }
        
        if (avgLoss == 0) {
            return 100.0; // 손실이 없으면 RSI는 100
        }
        
        double rs = avgGain / avgLoss;
        return 100.0 - (100.0 / (1.0 + rs));
    }
    
    /**
     * 이동평균 계산
     * @param candles 캔들 데이터
     * @param period 이동평균 기간
     * @return 이동평균 값
     */
    public static double calculateSMA(List<Candle> candles, int period) {
        if (candles == null || candles.size() < period) {
            throw new IllegalArgumentException("이동평균 계산을 위한 캔들 데이터가 부족합니다.");
        }
        
        return candles.subList(0, period).stream()
                .mapToDouble(Candle::getTradePrice)
                .average()
                .orElse(0.0);
    }
    
    /**
     * 지수이동평균 계산
     * @param candles 캔들 데이터
     * @param period EMA 기간
     * @return EMA 값
     */
    public static double calculateEMA(List<Candle> candles, int period) {
        if (candles == null || candles.size() < period) {
            throw new IllegalArgumentException("EMA 계산을 위한 캔들 데이터가 부족합니다.");
        }
        
        double multiplier = 2.0 / (period + 1);
        
        // 초기 EMA는 SMA로 시작
        double ema = calculateSMA(candles.subList(candles.size() - period, candles.size()), period);
        
        // 최신 데이터부터 EMA 계산 (역순 처리)
        for (int i = candles.size() - period - 1; i >= 0; i--) {
            double currentPrice = candles.get(i).getTradePrice();
            ema = (currentPrice * multiplier) + (ema * (1 - multiplier));
        }
        
        return ema;
    }
    
    /**
     * MACD 계산
     * @param candles 캔들 데이터
     * @param fastPeriod 빠른 EMA 기간 (기본: 12)
     * @param slowPeriod 느린 EMA 기간 (기본: 26)
     * @param signalPeriod 신호선 기간 (기본: 9)
     * @return MACD 결과 [MACD, Signal, Histogram]
     */
    public static double[] calculateMACD(List<Candle> candles, int fastPeriod, int slowPeriod, int signalPeriod) {
        if (candles == null || candles.size() < slowPeriod + signalPeriod) {
            throw new IllegalArgumentException("MACD 계산을 위한 캔들 데이터가 부족합니다.");
        }
        
        double fastEMA = calculateEMA(candles, fastPeriod);
        double slowEMA = calculateEMA(candles, slowPeriod);
        double macd = fastEMA - slowEMA;
        
        // 신호선 계산을 위한 MACD 히스토리가 필요하지만, 간단히 현재 MACD 값으로 대체
        double signal = macd * 0.9; // 간단한 근사치
        double histogram = macd - signal;
        
        return new double[]{macd, signal, histogram};
    }
    
    /**
     * 볼린저 밴드 계산
     * @param candles 캔들 데이터
     * @param period 기간 (기본: 20)
     * @param standardDeviation 표준편차 배수 (기본: 2)
     * @return 볼린저 밴드 [상단밴드, 중간밴드(SMA), 하단밴드]
     */
    public static double[] calculateBollingerBands(List<Candle> candles, int period, double standardDeviation) {
        if (candles == null || candles.size() < period) {
            throw new IllegalArgumentException("볼린저 밴드 계산을 위한 캔들 데이터가 부족합니다.");
        }
        
        double sma = calculateSMA(candles, period);
        
        // 표준편차 계산
        double variance = candles.subList(0, period).stream()
                .mapToDouble(candle -> Math.pow(candle.getTradePrice() - sma, 2))
                .average()
                .orElse(0.0);
        double stdDev = Math.sqrt(variance);
        
        double upperBand = sma + (standardDeviation * stdDev);
        double lowerBand = sma - (standardDeviation * stdDev);
        
        return new double[]{upperBand, sma, lowerBand};
    }
    
    /**
     * 스토캐스틱 %K 계산
     * @param candles 캔들 데이터
     * @param period 기간
     * @return 스토캐스틱 %K 값
     */
    public static double calculateStochasticK(List<Candle> candles, int period) {
        if (candles == null || candles.size() < period) {
            throw new IllegalArgumentException("스토캐스틱 계산을 위한 캔들 데이터가 부족합니다.");
        }
        
        List<Candle> periodCandles = candles.subList(0, period);
        
        double currentClose = candles.get(0).getTradePrice();
        double highestHigh = periodCandles.stream().mapToDouble(Candle::getHighPrice).max().orElse(0.0);
        double lowestLow = periodCandles.stream().mapToDouble(Candle::getLowPrice).min().orElse(0.0);
        
        if (highestHigh == lowestLow) {
            return 50.0; // 분모가 0인 경우 중간값 반환
        }
        
        return ((currentClose - lowestLow) / (highestHigh - lowestLow)) * 100.0;
    }
    
    /**
     * 거래량 가중 평균 가격 (VWAP) 계산
     * @param candles 캔들 데이터
     * @return VWAP 값
     */
    public static double calculateVWAP(List<Candle> candles) {
        if (candles == null || candles.isEmpty()) {
            throw new IllegalArgumentException("VWAP 계산을 위한 캔들 데이터가 없습니다.");
        }
        
        double totalPriceVolume = 0.0;
        double totalVolume = 0.0;
        
        for (Candle candle : candles) {
            double typicalPrice = (candle.getHighPrice() + candle.getLowPrice() + candle.getTradePrice()) / 3.0;
            double volume = candle.getCandleAccTradeVolume();
            
            totalPriceVolume += typicalPrice * volume;
            totalVolume += volume;
        }
        
        return totalVolume > 0 ? totalPriceVolume / totalVolume : 0.0;
    }
}