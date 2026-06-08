FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup && \
    mkdir -p /app/logs && chown -R appuser:appgroup /app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

USER appuser
EXPOSE 8088

ENTRYPOINT ["java","-jar","/app/app.jar"]
