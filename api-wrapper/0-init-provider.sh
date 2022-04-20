#!/bin/bash

set -o errexit
set -o errtrace
set -o pipefail
set -o nounset

# Create dummy data
curl -X POST -H 'Content-Type: text/plain' --data "test" http://localhost:8194/data/asset-1

# Create a asset
curl -X POST -H 'Content-Type: application/json' --data "@resources/asset.json" http://localhost:8187/api/v1/data/assets

# Create a general policy
curl -X POST -H 'Content-Type: application/json' --data "@resources/policy.json" http://localhost:8187/api/v1/data/policies

# Create a contract definition
curl -X POST -H 'Content-Type: application/json' --data "@resources/contractdefinition.json" http://localhost:8187/api/v1/data/contractdefinitions

