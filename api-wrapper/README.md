# Local build guide

## 1. Setup vault

```shell
docker-compose -f ./docker-compose.vault.yml up --build -d
```

## 2. Setup EDCs

Wait a view seconds until the vault was initialized.

```shell
docker-compose up -d
```

## 2.1 See logs and check if the services are already up

```shell
docker-compose -f ./docker-compose.vault.yml -f ./docker-compose.api-wrapper.yml -f ./docker-compose.yml logs -f
```


## 3. Run Script 0-init-provider.sh

```shell
./0-init-provider.sh
```

## 4. Build your local API wrapper

```shell
cd services/api-wrapper
./gradlew clean build
cd ../..
docker-compose -f ./docker-compose.api-wrapper.yml up --build -d
```

## 4. Run Script 1-aas-client-get.sh

```shell
./1-aas-client-get.sh
```

## Destroy everything

```shell
docker-compose -f ./docker-compose.vault.yml -f ./docker-compose.api-wrapper.yml -f ./docker-compose.yml down
```
