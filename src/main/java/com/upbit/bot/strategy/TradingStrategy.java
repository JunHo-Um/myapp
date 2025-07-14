package com.upbit.bot.strategy;

import com.upbit.bot.model.Candle;
import com.upbit.bot.model.Ticker;
import java.util.List;

/**
 * 매매 전략 인터페이스
 */
public interface TradingStrategy {
    
    /**
     * 매수 신호인지 판단
     * @param candles 캔들 데이터 리스트 (최신 데이터가 첫 번째)
     * @param ticker 현재가 정보
     * @return 매수 신호 여부
     */
    boolean shouldBuy(List<Candle> candles, Ticker ticker);
    
    /**
     * 매도 신호인지 판단
     * @param candles 캔들 데이터 리스트 (최신 데이터가 첫 번째)
     * @param ticker 현재가 정보
     * @return 매도 신호 여부
     */
    boolean shouldSell(List<Candle> candles, Ticker ticker);
    
    /**
     * 전략명 반환
     * @return 전략 이름
     */
    String getStrategyName();
    
    /**
     * 전략 설명 반환
     * @return 전략 설명
     */
    String getDescription();
}