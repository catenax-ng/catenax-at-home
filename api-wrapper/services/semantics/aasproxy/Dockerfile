FROM --platform=$BUILDPLATFORM openjdk:17-slim-buster
ARG JAR

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8090
ENV SPRING_PROFILES_ACTIVE local,basic
ENV AASPROXY_REGISTRYURL http://registry-service:4243
ENV AASPROXY_WRAPPERURL http://api-wrapper:9191/api/service

WORKDIR /app
COPY $JAR app.jar

EXPOSE 9191
EXPOSE 8090

ENTRYPOINT java \
    -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}\
    -Daasproxy.registryUrl=${AASPROXY_REGISTRYURL}\
    -Daasproxy.wrapperUrl=${AASPROXY_WRAPPERURL}\
    -Djava.security.edg=file:/dev/.urandom \
    -jar app.jar
