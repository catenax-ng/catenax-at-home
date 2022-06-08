package net.catenax.edc.apiwrapper.config;

public class BasicAuthVaultLabels {
    private final String username;
    private final String vaultKey;

    public BasicAuthVaultLabels(String username, String vaultKey) {
        this.username = username;
        this.vaultKey = vaultKey;
    }

    public String getUsername() {
        return username;
    }

    public String getVaultKey(){
        return vaultKey;
    }
}
