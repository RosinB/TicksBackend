# 使用官方的 OpenJDK 17 基礎映像
FROM openjdk:17-jdk-slim

# 設定工作目錄
WORKDIR /app

# 複製生成的 JAR 文件到容器中
COPY target/tickets-backend-0.0.1-SNAPSHOT.jar app.jar

# 暴露 Spring Boot 應用程式的埠
EXPOSE 8080

# 容器啟動時執行 JAR 文件
ENTRYPOINT ["java", "-jar", "app.jar"]