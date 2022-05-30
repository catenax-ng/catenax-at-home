# API-WRAPPER

## Basic authentication

This module contains an `AuthenticationService` implementation for basic authentication. It will be active when you 
provide at least one credential pair.

### Usage/Example

In your `Vault` instance you have to provide the password for each user. You can put them under any key you want, but we
would recommend using the following key format:

```plain
wraper-auth-basic-<USERNAME>
```
In your EDC configuration you must then specify the respective vault key for the respective user:

```properties
wrapper.auth.basic.usera: wrapper-auth-basic-usera
wrapper.auth.basic.userb: wrapper-auth-basic-userb
```
