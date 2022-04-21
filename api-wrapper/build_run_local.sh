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

export MAVEN_OPTS=(${GRADLE_PROPS[*]})

# build semantics
cd services/semantics
mvn clean install -DskipTests
# build api-wrapper
cd ../api-wrapper
chmod +x gradlew
./gradlew ${MAVEN_OPTS} clean build
# build backend-data-service
cd ../backend-data-service
chmod +x gradlew
./gradlew ${MAVEN_OPTS} clean build
# build control-plane
cd ../control-plane
chmod +x gradlew
./gradlew ${MAVEN_OPTS} clean build
# build data-plane
cd ../data-plane
chmod +x gradlew
./gradlew ${MAVEN_OPTS} clean build
# change directory to root 
cd ../..

# 
export DOCKER_PLATFORM=${DOCKER_PLATFORM:-linux/amd64}

# build docker 
docker-compose --file docker-compose.yml --project-name catenax_at_home --verbose build
docker-compose --file docker-compose.yml --project-name catenax_at_home --verbose up