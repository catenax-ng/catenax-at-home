package net.catenax.edc.apiwrapper.cache;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryContractAgreementCacheTest {

    @Test
    void cache_disabled() {
        // given
        String assetId = "random-asset-id";
        String agreementId = "random-agreement-id";
        InMemoryContractAgreementCache cache = new InMemoryContractAgreementCache(false);

        // when
        cache.put(assetId, agreementId);

        // then
        assertThat(cache.get(assetId)).isNull();
    }

    @Test
    void cache_enabled() {
        // given
        String assetId = "random-asset-id";
        String agreementId = "random-agreement-id";
        InMemoryContractAgreementCache cache = new InMemoryContractAgreementCache(true);

        // when
        cache.put(assetId, agreementId);

        // then
        assertThat(cache.get(assetId)).isEqualTo(agreementId);
    }
}
