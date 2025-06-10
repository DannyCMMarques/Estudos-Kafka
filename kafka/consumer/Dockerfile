# ---------- BUILD ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# ---------- RUNTIME ----------
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copia *qualquer* jar que exista em target e já renomeia para app.jar
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8081