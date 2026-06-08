FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# 创建日志目录
RUN mkdir -p /app/logs

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8088

ENTRYPOINT ["java","-jar","/app/app.jar"]
