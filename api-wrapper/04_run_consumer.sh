#!/bin/sh
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

docker-compose --file docker-compose-consumer.yml --project-name consumer_catenax_at_home up
