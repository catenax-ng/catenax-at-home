#!/usr/bin/env bash

version="0.0.1"

docker tag api-wrapper_consumer-control-plane ghcr.io/catenax-ng/catenax-at-home/consumer-control-plane:${version}
docker tag api-wrapper_consumer-data-plane ghcr.io/catenax-ng/catenax-at-home/consumer-data-plane:${version}
docker tag api-wrapper_api-wrapper ghcr.io/catenax-ng/catenax-at-home/api-wrapper:${version}
docker tag api-wrapper_provider-control-plane ghcr.io/catenax-ng/catenax-at-home/provider-control-plane:${version}
docker tag api-wrapper_provider-data-plane ghcr.io/catenax-ng/catenax-at-home/provider-data-plane:${version}
docker tag api-wrapper_provider-backend-service ghcr.io/catenax-ng/catenax-at-home/provider-backend-service:${version}
docker tag api-wrapper_consumer-aas-proxy-service ghcr.io/catenax-ng/catenax-at-home/consumer-aas-proxy-service:${version}
docker tag ghcr.io/catenax/semantics/registry:catenax-at-home-latest ghcr.io/catenax-ng/catenax-at-home/registry:${version}

docker push ghcr.io/catenax-ng/catenax-at-home/consumer-control-plane:${version}
docker push ghcr.io/catenax-ng/catenax-at-home/consumer-data-plane:${version}
docker push ghcr.io/catenax-ng/catenax-at-home/api-wrapper:${version}
docker push ghcr.io/catenax-ng/catenax-at-home/provider-control-plane:${version}
docker push ghcr.io/catenax-ng/catenax-at-home/provider-data-plane:${version}
docker push ghcr.io/catenax-ng/catenax-at-home/provider-backend-service:${version}
docker push ghcr.io/catenax-ng/catenax-at-home/consumer-aas-proxy-service:${version}
docker push ghcr.io/catenax-ng/catenax-at-home/registry:${version}
