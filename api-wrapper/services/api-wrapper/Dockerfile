FROM openjdk:11-jre-slim-buster
ARG JAR

WORKDIR /app
COPY $JAR app.jar

EXPOSE 9191

ENTRYPOINT java \
    -Djava.security.edg=file:/dev/.urandom \
    -jar app.jar
