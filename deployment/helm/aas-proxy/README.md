# AAS-Proxy Helm Chart

## Important configurations

In addition to the "normal" Helm/Kubernetes configurations, you must specify the following values:

```yaml
proxy:
  publicProxyUrl:
  registryUrl:
  wrapperUrl:
```

Example values could be:

```yaml
proxy:
  publicProxyUrl: https://aas-proxy.consumer-domain.com
  registryUrl: https://aas-registry.global-services-domain.com
  wrapperUrl: http://api-wrapper:9191/api/service
```

The `proxy.wrapperUrl` can be cluster internal as it isn't exposed to the outer world.

The `proxy.publicProxyUrl` will be used to rewrite the EDC Host within the registry response to the AAS-Proxy url which
then will be called from the client application. Therefor it could make sense to set this to the same value as the
variable `ingress.hostname` Of course, if the client application also sits within the cluster, this can be a cluster
internal url.
