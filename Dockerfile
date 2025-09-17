# ====== BUILD STAGE ======
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

# ====== RUN STAGE ======
FROM eclipse-temurin:17-jre-alpine AS run
WORKDIR /app

# Без рута — хорошая практика
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Копируем fat-jar, собранный на предыдущем этапе
COPY --from=build /app/target/*.jar /app/app.jar

# Приложение слушает 8080
EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport"

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
