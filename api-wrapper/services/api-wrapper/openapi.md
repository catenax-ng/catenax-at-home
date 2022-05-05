# Generating the OpenApi Spec (*.yaml)


We use the Gradle plugins `io.swagger.core.v3.swagger-gradle-plugin` to generate the `openapi/openApi.yaml` file.
This plugin scans all our REST endpoints for Jakarta Annotations which are then used to generate the
`openapi/openApi.yaml` specification.

To re-generate this file, simply invoke

```shell
./gradlew clean resolve
```

## Note of omission

This feature does **neither** expose the generated files through a REST endpoint, nor does it serve static
web content with the ever-so-popular Swagger UI.

Furthermore, **no** client code is auto-generated, as this will be highly dependent on the frameworks used 
on the client side. 

A pointer on how to expose the YAML file and the Swagger UI using Jetty can be found
[here](https://anirtek.github.io/java/jetty/swagger/openapi/2021/06/12/Hooking-up-OpenAPI-with-Jetty.html).

To just take a quick look at the generated API documentation with Swagger UI, you can run it in a Docker container or by
manually copy it into the online Swagger Editor (<https://editor.swagger.io/>).

```shell
docker run -p 80:8080 -e SWAGGER_JSON=/openapi.yaml -v $(pwd)resources/openapi/openapi.yaml:/openapi.yaml swaggerapi/swagger-ui
```
