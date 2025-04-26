# Build Stage
FROM gradle:8.13-jdk21-corretto AS build
WORKDIR /app
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle gradle
RUN ./gradlew dependencies --no-daemon || true
COPY src src
RUN ./gradlew clean build -x test --no-daemon

# Run Stage
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/build/libs/* member-service.jar
ENTRYPOINT ["java", "-jar", "member-service.jar"]
