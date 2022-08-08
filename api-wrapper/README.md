# Local build guide

## 1. Setup vault

```shell
docker-compose -f ./docker-compose.vault.yml --build -d 
```

## 2. Setup EDCs

Wait a view seconds until the vault was initialized.

```shell
docker-compose up -d
```

## See logs

```shell
docker-compose -f ./docker-compose.vault.yml -f ./docker-compose.yml logs -f
```

## Destroy everything

```shell
docker-compose -f ./docker-compose.vault.yml -f ./docker-compose.yml down
```
