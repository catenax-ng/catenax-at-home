package net.catenax.edc.apiwrapper.cache;

import java.util.HashMap;
import java.util.Map;

public class InMemoryContractAgreementCache {
    private final Map<String, String> store = new HashMap<>();
    private final Boolean enabled;

    public InMemoryContractAgreementCache(Boolean enabled) {
        this.enabled = enabled;
    }

    public void put(String assetId, String contractAgreementId) {
        if (Boolean.TRUE.equals(enabled)) {
            store.put(assetId, contractAgreementId);
        }
    }

    public String get(String assetId) {
        return Boolean.TRUE.equals(enabled) ? store.get(assetId) : null;
    }
}
