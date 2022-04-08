#!/bin/bash

set -o errexit
set -o errtrace
set -o pipefail
set -o nounset

# Create and register Simple Assets
curl -X POST -H 'Content-Type: application/json' --data "@resources/asset1.json" http://localhost:8187/api/v1/data/assets
#curl -X POST -H 'Content-Type: application/json' --data "@resources/asset2.json" http://localhost:8187/api/v1/data/assets

# Create and register Contract Definition
#curl -X POST -H 'Content-Type: application/json' --data "@resources/contractdefinition.json" http://localhost:8187/api/contractdefinitions
