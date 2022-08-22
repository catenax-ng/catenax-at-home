#!/usr/bin/env sh

set -o errexit
set -o nounset

dpSignerPrivateKeyAlias="dp-signer-private-key"
dpVerifierPublicKeyAlias="dp-verifier-public-key"
dpSignerPrivateKeyPath="${dpSignerPrivateKeyAlias}.pem"
dpVerifierPublicKeyPath="${dpVerifierPublicKeyAlias}.pem"

dataEncryptionKeyAlias="data-encryption"
dataEncryptionKeyPath="${dataEncryptionKeyAlias}.aes"

[ -z "$VAULT_URL" ] && VAULT_URL="http://localhost:8200"
[ -z "$VAULT_TOKEN" ] && VAULT_TOKEN="password"

# Check dependencies
if ! which curl > /dev/null; then
  printf "curl command is missing\n"
fi
if ! which jq > /dev/null; then
  printf "jq command is missing\n"
  return 1
fi
if ! which vault > /dev/null; then
  printf "vault command is missing\n"
  return 1
fi

echo "Generating the keys"
# Private Key
openssl genpkey -out "${dpSignerPrivateKeyPath}" -algorithm RSA -pkeyopt rsa_keygen_bits:2048
# Public Key
openssl rsa -in "${dpSignerPrivateKeyPath}" -out "${dpVerifierPublicKeyPath}" -pubout -outform PEM

# DataEncryption Key
openssl rand -base64 32 > "${dataEncryptionKeyPath}"

echo "Waiting for Vault..."
while [ "$(curl -XGET --insecure --silent -H "X-Vault-Token: ${VAULT_TOKEN}" "${VAULT_URL}/v1/sys/health" | jq '.initialized')" != "true" ]; do
    echo 'Vault is Initializing...'
    sleep 2
done

echo "Vault Started."

echo "Authenticate into Vault"
vault login -non-interactive=true -no-print -address="${VAULT_URL}" token="${VAULT_TOKEN}"

echo "Adding secrets to Vault..."
vault kv put -address="${VAULT_URL}" "secret/${dpSignerPrivateKeyAlias}" content=- < "${dpSignerPrivateKeyPath}"
vault kv put -address="${VAULT_URL}" "secret/${dpVerifierPublicKeyAlias}" content=- < "${dpVerifierPublicKeyPath}"
vault kv put -address="${VAULT_URL}" "secret/${dataEncryptionKeyAlias}" content=- < "${dataEncryptionKeyPath}"
