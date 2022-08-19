# Backend Data Service

## Debugging

You need to set this environment variable in your deployment:

```
JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
```
