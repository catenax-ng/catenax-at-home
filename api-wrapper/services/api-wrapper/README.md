Building the API Wrapper and the AAS-Proxy

In order to build the API Wrapper and the AAS-Proxy you need to build first the EDC Modules extensions from the Data 
Space Connector. There is a specific version of the EDC that you need to download and this version is the one of 
Product-EDC (https://github.com/catenax-ng/product-edc). Start cloning the product with the following command:

git clone https://github.com/catenax-ng/product-edc.git

After cloning the repository you have to locally publish the edc components via maven. It is made using the following 
command:

cd edc && ./gradlew publishToMavenLocal

Once the artifacts are locally published, it is possible to build the Api-Wrapper and the AAS-Proxy. In order to do 
this, go to the folder services of catena-x@Home:

cd $Folder_containingCatenaXAtHome$/catenax-at-home/api-wrapper/services

then go to the api-wrapper folder

cd api-wrapper

now start building the api-wrapper using the following command :

./gradlew clean build

or

./gradlew clean build -x test (if you do not want to do tests of the module)

now return to the folder services and go to the subfolder aasproxy

cd.. && cd semantics/aasproxy

and use the same command used to build the api-wrapper 



