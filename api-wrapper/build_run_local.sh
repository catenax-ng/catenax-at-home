#
# Copyright (c) 2022 SAP SE (Catena-X Consortium)
#
# See the AUTHORS file(s) distributed with this work for additional
# information regarding authorship.
#
# See the LICENSE file(s) distributed with this work for
# additional information regarding license terms.
#

#
# Shell script to build and run Catena-X@Home on your local container environment.
#
# Prerequisites:
#   Windows, (git)-bash shell, java 11 (java) and maven (mvn) in the $PATH.
#   Unix, zsh, java 11 (java) and maven (mvn) in the $PATH.
#
# Synposis:
#   ./build_run_local.sh
#
# Comments:
#   
#

#############################
# Clone OOS EDC and build sources
# |-> Milestone 3.1
# https://github.com/eclipse-dataspaceconnector/DataSpaceConnector/tree/milestone-3.1
#############################
echo "01: --> CLONE AND BUILD EDC SOURCES"
DIR="DataSpaceConnector"
if [ -d "$DIR" ]; then
  rm -rf $DIR
  echo "Folder ${DIR} already exists: Deleting ${DIR}"
fi
git clone --branch milestone-3.1 --single-branch https://github.com/eclipse-dataspaceconnector/DataSpaceConnector.git
cd $DIR
./gradlew clean build publishToMavenLocal -x test
cd ..

#############################
# Build Semantics
#############################
echo "02: --> BUILD SEMANTICS SOURCES"
cd services/semantics
./mvnw clean install -DskipTests

#############################
# Build api-wrapper
#############################
echo "03: --> BUILD API-WRAPPER SOURCES"
cd ../api-wrapper
./gradlew clean build -x test

#############################
# Build backend-data-service
#############################
echo "04: --> BUILD BACKEND-DATA-SERVICE SOURCES"
cd ../backend-data-service
./gradlew clean build -x test

#############################
# Build control-plane
#############################
echo "05: --> BUILD CONTROL-PLANE SOURCES"
cd ../control-plane
./gradlew clean build

#############################
# Build data-plane
#############################
echo "06: --> BUILD DATA-PLANE SOURCES"
cd ../data-plane
./gradlew clean build -x test

##############################
# Docker Compose Build and Run
##############################
# change directory to root 
cd ../..
echo "07: --> DOCKER COMPOSE BUILD AND RUN LOCAL CONTEXT"
docker-compose --file docker-compose.yml --project-name catenax_at_home --verbose build

docker-compose --file docker-compose-consumer.yml --project-name consumer_catenax_at_home up &
docker-compose --file docker-compose-provider.yml --project-name provider_catenax_at_home up &
docker-compose --file docker-compose-centralservices.yml --project-name centralservices_catenax_at_home up &

#docker-compose --file docker-compose.yml --project-name catenax_at_home --verbose up
