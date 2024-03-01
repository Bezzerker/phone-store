FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /home/app-source-code
COPY . .
RUN mvn clean package -DskipTests=true

FROM eclipse-temurin:17.0.10_7-jre-jammy
WORKDIR /usr/local/phone-store-application
COPY --from=builder /home/app-source-code/target/sber-task-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "sber-task-0.0.1-SNAPSHOT.jar"]