FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# 创建日志目录
RUN mkdir -p /app/logs

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# 添加MariaDB客户端工具（用于健康检查）
RUN apk add --no-cache mariadb-client

EXPOSE 8088

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8088/api/actuator/health || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar","--jasypt.encryptor.password=SecretKey_jasper_mariadb"]
