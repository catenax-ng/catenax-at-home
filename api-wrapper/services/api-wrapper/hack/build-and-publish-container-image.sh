#!/usr/bin/env bash

wrapperPath="../"
wrapperVersion="0.0.8-SNAPSHOT"
wrapperContainerImage="ghcr.io/catenax-ng/catenax-at-home/consumer-api-wrapper"

cd "${wrapperPath}" || exit 1
./gradlew clean build
cd - || exit 1

docker buildx build --platform linux/amd64 --build-arg JAR=build/libs/edc.jar -t "${wrapperContainerImage}:${wrapperVersion}" "${wrapperPath}"
#docker push "${wrapperContainerImage}:${wrapperVersion}"
