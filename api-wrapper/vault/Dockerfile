FROM vault:1.11.2

RUN apk add --no-cache curl jq openssl

ENV VAULT_URL http://hashicorp-vault:8200
ENV VAULT_TOKEN password

ADD uploadCertsIntoVault.sh /

RUN chmod 755 uploadCertsIntoVault.sh

ENTRYPOINT exec "./uploadCertsIntoVault.sh"
