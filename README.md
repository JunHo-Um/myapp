# 업비트 자동 매매 봇 (Upbit Trading Bot)

Java로 작성된 업비트 암호화폐 자동 매매 프로그램입니다. RSI 기반 기술적 분석을 통해 자동으로 매수/매도 신호를 감지하고 거래를 실행합니다.

## 🚀 주요 기능

- **RSI 기반 매매 전략**: RSI 지표를 활용한 과매수/과매도 구간 매매
- **리스크 관리**: 손절매, 익절매, 일일 최대 손실 한도 설정
- **모의 거래 모드**: 실제 거래 없이 전략 테스트 가능
- **실시간 모니터링**: 상세한 로깅과 거래 내역 추적
- **자동 포지션 관리**: 현재 보유 상태에 따른 자동 매수/매도 판단

## 📋 요구사항

- Java 11 이상
- Maven 3.6 이상
- 업비트 API 키 (실제 거래시)

## 🛠️ 설치 및 실행

### 1. 프로젝트 클론
```bash
git clone <repository-url>
cd upbit-trading-bot
```

### 2. 의존성 설치
```bash
mvn clean install
```

### 3. API 키 설정 (실제 거래시)
환경변수로 업비트 API 키를 설정하세요:

```bash
export UPBIT_ACCESS_KEY="your_access_key"
export UPBIT_SECRET_KEY="your_secret_key"
```

API 키가 설정되지 않으면 자동으로 모의 거래 모드로 실행됩니다.

### 4. 실행
```bash
# Maven으로 실행
mvn exec:java -Dexec.mainClass="com.upbit.bot.UpbitTradingBot"

# 또는 JAR 파일로 실행
mvn package
java -jar target/upbit-trading-bot-1.0.0.jar
```

## ⚙️ 설정

`TradingConfig` 클래스에서 다음 설정을 조정할 수 있습니다:

### 매매 설정
- `targetMarket`: 거래할 마켓 (기본: "KRW-BTC")
- `investmentRatio`: 투자 비율 (기본: 0.1, 전체 자금의 10%)
- `minInvestmentAmount`: 최소 투자 금액 (기본: 5,000원)
- `maxInvestmentAmount`: 최대 투자 금액 (기본: 1,000,000원)

### 전략 설정
- `candleInterval`: 캔들 간격 (기본: 5분)
- `candleCount`: 조회할 캔들 개수 (기본: 100개)
- `tradingInterval`: 매매 주기 (기본: 60초)

### 리스크 관리
- `stopLossPercentage`: 손절 비율 (기본: -5%)
- `takeProfitPercentage`: 익절 비율 (기본: 10%)
- `maxDailyLoss`: 일일 최대 손실률 (기본: -20%)

## 📊 RSI 전략 설명

이 봇은 RSI(Relative Strength Index) 기반 매매 전략을 사용합니다:

### 매수 조건
- RSI ≤ 30 (과매도 구간)
- 24시간 거래량 ≥ 100만원
- 최근 가격이 상승 추세

### 매도 조건
- RSI ≥ 70 (과매수 구간) 또는
- 현재가 대비 5% 이상 하락 (손절)

## 📁 프로젝트 구조

```
src/main/java/com/upbit/bot/
├── api/                    # API 클라이언트
│   └── UpbitApiClient.java
├── config/                 # 설정 클래스
│   └── TradingConfig.java
├── model/                  # 데이터 모델
│   ├── Account.java
│   ├── Candle.java
│   ├── Market.java
│   ├── Order.java
│   ├── OrderChance.java
│   ├── OrderRequest.java
│   ├── Orderbook.java
│   └── Ticker.java
├── strategy/               # 매매 전략
│   ├── TradingStrategy.java
│   └── RSIStrategy.java
├── util/                   # 유틸리티
│   └── TechnicalAnalysis.java
└── UpbitTradingBot.java   # 메인 클래스
```

## 📝 로그

로그 파일은 `logs/` 디렉토리에 저장됩니다:
- `upbit-trading-bot.log`: 일반 로그
- `trades.log`: 거래 전용 로그

## ⚠️ 주의사항

1. **실제 거래 주의**: 이 프로그램은 실제 돈을 거래합니다. 충분한 테스트 후 사용하세요.
2. **API 키 보안**: API 키를 코드에 하드코딩하지 마세요. 환경변수를 사용하세요.
3. **리스크 관리**: 투자 금액과 리스크 설정을 신중히 조정하세요.
4. **모의 거래 테스트**: 실제 거래 전에 반드시 모의 거래로 테스트하세요.

## 🔒 업비트 API 키 발급

1. [업비트](https://upbit.com) 로그인
2. 마이페이지 → Open API 관리
3. API 키 발급
4. 필요한 권한 설정:
   - 자산 조회
   - 주문 조회
   - 주문하기

## 📈 기술적 지표

`TechnicalAnalysis` 클래스에서 제공하는 지표들:
- RSI (Relative Strength Index)
- SMA (Simple Moving Average)
- EMA (Exponential Moving Average)
- MACD (Moving Average Convergence Divergence)
- 볼린저 밴드 (Bollinger Bands)
- 스토캐스틱 (Stochastic)
- VWAP (Volume Weighted Average Price)

## 🤝 기여하기

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## ⚖️ 면책조항

이 소프트웨어는 교육 목적으로 제공됩니다. 실제 거래로 인한 손실에 대해 개발자는 책임지지 않습니다. 투자는 본인의 판단과 책임 하에 이루어져야 합니다.

## 📞 문의

버그 리포트나 기능 요청은 GitHub Issues를 통해 제출해 주세요.