#!/bin/bash
# 멀티플랫폼 Docker 이미지 빌드 및 Docker Hub 푸시 스크립트
# ARM64(Mac) + AMD64(Linux) 모두 지원

set -e

# 색상 코드
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=====================================${NC}"
echo -e "${BLUE}  멀티플랫폼 Docker 이미지 빌드${NC}"
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

echo -e "${YELLOW}📦 이미지 이름: ${FULL_IMAGE_NAME}${NC}"
echo -e "${YELLOW}🌍 지원 플랫폼: linux/amd64, linux/arm64${NC}"
echo ""

# Docker 로그인 확인
echo -e "${BLUE}🔐 Docker Hub 로그인 확인 중...${NC}"
if ! docker info | grep -q "Username"; then
    echo -e "${YELLOW}⚠️  Docker Hub에 로그인되어 있지 않습니다.${NC}"
    echo -e "${YELLOW}   로그인을 진행합니다...${NC}"
    docker login
fi
echo -e "${GREEN}✅ Docker Hub 로그인 확인 완료${NC}"
echo ""

# 로컬에서 JAR 빌드
echo -e "${BLUE}📦 로컬에서 JAR 파일 빌드 중...${NC}"
./gradlew clean bootJar -x test
echo -e "${GREEN}✅ JAR 빌드 완료${NC}"
echo ""

# Buildx 빌더 확인 및 생성
echo -e "${BLUE}🔨 Buildx 빌더 확인 중...${NC}"
BUILDER_NAME="multiplatform-builder"

if ! docker buildx ls | grep -q "$BUILDER_NAME"; then
    echo -e "${YELLOW}⚠️  Buildx 빌더가 없습니다. 생성합니다...${NC}"
    docker buildx create --name $BUILDER_NAME --use --bootstrap
    echo -e "${GREEN}✅ Buildx 빌더 생성 완료${NC}"
else
    echo -e "${GREEN}✅ Buildx 빌더 이미 존재${NC}"
    docker buildx use $BUILDER_NAME
fi
echo ""

# 빌드 시작
echo -e "${BLUE}🏗️  멀티플랫폼 이미지 빌드 및 푸시 시작...${NC}"
echo -e "${YELLOW}   이 작업은 몇 분 정도 소요될 수 있습니다.${NC}"
echo ""

# 현재 디렉토리 확인
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

docker buildx build --platform linux/amd64,linux/arm64 --tag ${FULL_IMAGE_NAME} --push .

echo ""
echo -e "${GREEN}✅ 빌드 및 푸시 완료!${NC}"
echo ""
echo -e "${BLUE}=====================================${NC}"
echo -e "${GREEN}🎉 성공적으로 Docker Hub에 업로드되었습니다!${NC}"
echo -e "${BLUE}=====================================${NC}"
echo ""
echo -e "${YELLOW}📋 이미지 정보:${NC}"
echo -e "   이름: ${FULL_IMAGE_NAME}"
echo -e "   플랫폼: linux/amd64, linux/arm64"
echo -e "   Docker Hub URL: https://hub.docker.com/r/${DOCKER_USERNAME}/${IMAGE_NAME}"
echo ""
echo -e "${YELLOW}🚀 서버에서 실행 방법:${NC}"
echo -e "   ${GREEN}docker pull ${FULL_IMAGE_NAME}${NC}"
echo -e "   ${GREEN}docker run -d --name todolist-api -p 8080:8080 ${FULL_IMAGE_NAME}${NC}"
echo ""
