#!/usr/bin/env bash

edcVersion="0.0.1"
proxyVersion="0.0.2"
wrapperVersion="0.0.2"
backendVersion="0.0.1"
registryVersion="0.0.1"

docker tag api-wrapper_consumer-control-plane ghcr.io/catenax-ng/catenax-at-home/consumer-control-plane:${edcVersion}
docker tag api-wrapper_consumer-data-plane ghcr.io/catenax-ng/catenax-at-home/consumer-data-plane:${edcVersion}
docker tag api-wrapper_consumer-aas-proxy ghcr.io/catenax-ng/catenax-at-home/consumer-aas-proxy:${proxyVersion}
docker tag api-wrapper_consumer-api-wrapper ghcr.io/catenax-ng/catenax-at-home/consumer-api-wrapper:${wrapperVersion}
docker tag api-wrapper_provider-control-plane ghcr.io/catenax-ng/catenax-at-home/provider-control-plane:${edcVersion}
docker tag api-wrapper_provider-data-plane ghcr.io/catenax-ng/catenax-at-home/provider-data-plane:${edcVersion}
docker tag api-wrapper_provider-backend-service ghcr.io/catenax-ng/catenax-at-home/provider-backend-service:${backendVersion}
docker tag ghcr.io/catenax/semantics/registry:catenax-at-home-latest ghcr.io/catenax-ng/catenax-at-home/registry:${registryVersion}

docker push ghcr.io/catenax-ng/catenax-at-home/consumer-control-plane:${edcVersion}
docker push ghcr.io/catenax-ng/catenax-at-home/consumer-data-plane:${edcVersion}
docker push ghcr.io/catenax-ng/catenax-at-home/consumer-aas-proxy:${proxyVersion}
docker push ghcr.io/catenax-ng/catenax-at-home/consumer-api-wrapper:${wrapperVersion}
docker push ghcr.io/catenax-ng/catenax-at-home/provider-control-plane:${edcVersion}
docker push ghcr.io/catenax-ng/catenax-at-home/provider-data-plane:${edcVersion}
docker push ghcr.io/catenax-ng/catenax-at-home/provider-backend-service:${backendVersion}
docker push ghcr.io/catenax-ng/catenax-at-home/registry:${registryVersion}
