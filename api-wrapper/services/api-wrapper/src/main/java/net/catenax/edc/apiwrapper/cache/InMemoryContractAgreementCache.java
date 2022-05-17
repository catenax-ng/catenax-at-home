package net.catenax.edc.apiwrapper.cache;

import java.util.HashMap;
import java.util.Map;

public class InMemoryContractAgreementCache {
    private final Map<String, String> store = new HashMap<>();

    public void put(String assetId, String contractAgreementId) {
        store.put(assetId, contractAgreementId);
    }

    public String get(String assetId) {
        return store.get(assetId);
    }
}
