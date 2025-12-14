FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn dependency:go-offline -B

RUN mvn clean package

FROM eclipse-temurin:21 AS runner

WORKDIR /app
COPY --from=builder /app/target/Analytics-Service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 4002

ENTRYPOINT ["java", "-jar", "app.jar"]
