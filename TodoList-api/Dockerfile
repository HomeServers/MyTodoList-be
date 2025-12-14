# 간단한 런타임 전용 Dockerfile
# 로컬에서 빌드된 JAR 파일 사용
FROM eclipse-temurin:17-jre

# 작업 디렉토리 설정
WORKDIR /app

# 애플리케이션 실행을 위한 사용자 생성 (보안)
RUN groupadd -r spring && useradd -r -g spring spring

# 로컬에서 빌드된 JAR 파일 복사
COPY build/libs/*.jar app.jar

# 소유권 변경
RUN chown spring:spring app.jar

USER spring:spring

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
