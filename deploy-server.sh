#!/bin/bash
# Linux 서버에서 Docker Hub로부터 이미지를 pull하고 실행하는 스크립트

set -e

# 색상 코드
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=====================================${NC}"
echo -e "${BLUE}  todolist-api 배포${NC}"
echo -e "${BLUE}=====================================${NC}"
echo ""

# Docker Hub username 입력받기
read -p "Docker Hub username을 입력하세요: " DOCKER_USERNAME

if [ -z "$DOCKER_USERNAME" ]; then
    echo -e "${RED}❌ Username이 입력되지 않았습니다.${NC}"
    exit 1
fi

# 버전 입력받기
read -p "버전 태그를 입력하세요 (기본값: latest): " VERSION
VERSION=${VERSION:-latest}  # 입력이 없으면 latest 사용

# 이미지 이름 및 태그 설정
IMAGE_NAME="todolist-api"
FULL_IMAGE_NAME="${DOCKER_USERNAME}/${IMAGE_NAME}:${VERSION}"
CONTAINER_NAME="todolist-api"
NETWORK_NAME="todolist-network"

echo -e "${YELLOW}📦 이미지: ${FULL_IMAGE_NAME}${NC}"
echo -e "${YELLOW}🌐 네트워크: ${NETWORK_NAME}${NC}"
echo ""

# Docker 네트워크 확인 및 생성
echo -e "${BLUE}🔍 Docker 네트워크 확인 중...${NC}"
if ! docker network ls | grep -q "$NETWORK_NAME"; then
    echo -e "${YELLOW}⚠️  네트워크가 존재하지 않습니다. 생성합니다...${NC}"
    docker network create $NETWORK_NAME
    echo -e "${GREEN}✅ 네트워크 생성 완료${NC}"
else
    echo -e "${GREEN}✅ 네트워크 확인 완료${NC}"
fi
echo ""

# 기존 컨테이너 확인 및 중지
if docker ps -a | grep -q "$CONTAINER_NAME"; then
    echo -e "${YELLOW}⚠️  기존 컨테이너가 존재합니다. 중지 및 삭제합니다...${NC}"
    docker stop $CONTAINER_NAME 2>/dev/null || true
    docker rm $CONTAINER_NAME 2>/dev/null || true
    echo -e "${GREEN}✅ 기존 컨테이너 삭제 완료${NC}"
    echo ""
fi

# 최신 이미지 pull
echo -e "${BLUE}📥 Docker Hub로부터 이미지 다운로드 중...${NC}"
docker pull ${FULL_IMAGE_NAME}
echo -e "${GREEN}✅ 이미지 다운로드 완료${NC}"
echo ""

# 컨테이너 실행 (application.properties 설정 사용)
echo -e "${BLUE}🚀 컨테이너 실행 중...${NC}"
echo -e "${YELLOW}💡 application.properties에 설정된 DB 정보를 사용합니다.${NC}"
echo ""

docker run -d \
    --name $CONTAINER_NAME \
    --network $NETWORK_NAME \
    --restart unless-stopped \
    -p 8080:8080 \
    ${FULL_IMAGE_NAME}

echo -e "${GREEN}✅ 컨테이너 실행 완료${NC}"
echo ""

# 상태 확인
echo -e "${BLUE}📊 컨테이너 상태 확인 중...${NC}"
sleep 3
docker ps | grep $CONTAINER_NAME

echo ""
echo -e "${BLUE}=====================================${NC}"
echo -e "${GREEN}🎉 배포 완료!${NC}"
echo -e "${BLUE}=====================================${NC}"
echo ""
echo -e "${YELLOW}📋 유용한 명령어:${NC}"
echo -e "   로그 확인:       ${GREEN}docker logs -f $CONTAINER_NAME${NC}"
echo -e "   상태 확인:       ${GREEN}docker ps | grep $CONTAINER_NAME${NC}"
echo -e "   네트워크 확인:   ${GREEN}docker network inspect $NETWORK_NAME${NC}"
echo -e "   컨테이너 중지:   ${GREEN}docker stop $CONTAINER_NAME${NC}"
echo -e "   컨테이너 재시작: ${GREEN}docker restart $CONTAINER_NAME${NC}"
echo ""
echo -e "${YELLOW}🌐 네트워크 정보:${NC}"
echo -e "   컨테이너는 ${GREEN}${NETWORK_NAME}${NC} 네트워크에 연결되어 있습니다."
echo -e "   같은 네트워크의 다른 컨테이너와 통신할 수 있습니다."
echo ""
echo -e "${YELLOW}🔍 API 테스트:${NC}"
echo -e "   ${GREEN}curl http://localhost:8080/actuator/health${NC}"
echo ""

# 로그 출력 시작
echo -e "${YELLOW}📄 실시간 로그 (Ctrl+C로 종료):${NC}"
echo ""
docker logs -f $CONTAINER_NAME
