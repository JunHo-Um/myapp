#!/bin/bash

# 업비트 자동 매매 봇 실행 스크립트

echo "=== 업비트 자동 매매 봇 ==="
echo "주의: 이 프로그램은 실제 거래를 수행할 수 있습니다."
echo "충분한 테스트 후 사용하시기 바랍니다."
echo ""

# API 키 확인
if [ -z "$UPBIT_ACCESS_KEY" ] || [ -z "$UPBIT_SECRET_KEY" ]; then
    echo "⚠️  API 키가 설정되지 않았습니다."
    echo "모의 거래 모드로 실행됩니다."
    echo ""
    echo "실제 거래를 원하시면 다음 환경변수를 설정하세요:"
    echo "export UPBIT_ACCESS_KEY=\"your_access_key\""
    echo "export UPBIT_SECRET_KEY=\"your_secret_key\""
    echo ""
else
    echo "✅ API 키가 설정되었습니다."
    echo "⚠️  실제 거래 모드로 실행됩니다."
    echo ""
fi

# 필요한 디렉토리 생성
mkdir -p logs
mkdir -p target/classes

echo "📦 필요한 라이브러리를 다운로드하는 중..."
echo "실제 환경에서는 Maven을 사용하여 의존성을 관리하세요."
echo ""

echo "🚀 업비트 자동 매매 봇을 시작합니다..."
echo ""
echo "종료하려면 Ctrl+C를 누르세요."
echo ""

# 실제로는 Maven으로 빌드된 JAR를 실행해야 합니다
echo "Maven을 설치하고 다음 명령어로 실행하세요:"
echo "mvn clean package"
echo "java -jar target/upbit-trading-bot-1.0.0.jar"