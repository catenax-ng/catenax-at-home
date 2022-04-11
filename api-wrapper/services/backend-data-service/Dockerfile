FROM registry.cloud.zf-world.com/zf/zf-ubi8-openjdk-11
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/backend-data-service.jar"]
COPY build/libs/backend-data-service.jar /app/backend-data-service.jar
