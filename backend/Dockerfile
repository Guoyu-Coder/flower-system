# 多阶段构建：构建阶段
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn clean package -DskipTests -q

# 运行阶段
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/flower-shop-backend-1.0.0.jar app.jar

EXPOSE 8088

ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

CMD ["java", "-jar", "app.jar"]
