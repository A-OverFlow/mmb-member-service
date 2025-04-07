# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 캐시를 활용하기 위해 설정 파일 먼저 복사
COPY gradlew ./
COPY gradle gradle
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Gradle 실행 권한 추가
RUN chmod +x gradlew

# 종속성 캐시 생성 (빌드 속도 향상)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src src

# 프로젝트 빌드 (테스트 제외)
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: Run
FROM eclipse-temurin:21-jre

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 복사 (이름 패턴 수정)
COPY --from=builder /app/build/libs/*.jar /app/member-service.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/member-service.jar"]