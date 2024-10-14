FROM gradle:8.0.0-jdk17 AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradlew ./
COPY gradle ./gradle/
COPY src ./src/

RUN ./gradlew build -x test --no-daemon

FROM openjdk:17

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
