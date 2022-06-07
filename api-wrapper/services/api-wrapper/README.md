# Building the API Wrapper

To build the API Wrapper you need to build first the modules of the
[DataSpace Connector](https://github.com/eclipse-dataspaceconnector/DataSpaceConnector/). To be aligned with the
Catena-X related version of the EDC we suggest, that you checkout the
[product-edc](https://github.com/catenax-ng/product-edc) repository and use the EDC version which is checked in there as
a git submodule.

Building the edc modules and publishing it to mavens local cache:

```shell
git clone https://github.com/catenax-ng/product-edc.git
cd product-edc
git checkout 0.0.3
git submodule update --init
cd edc
./gradlew -publishToMavenLocal
```

Building the API Wrapper:

```shell
cd api-wrapper/services/api-wrapper
./gradlew clean build -x test
```
