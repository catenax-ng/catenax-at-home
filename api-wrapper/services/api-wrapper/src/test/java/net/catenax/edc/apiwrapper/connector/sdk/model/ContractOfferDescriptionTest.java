package net.catenax.edc.apiwrapper.connector.sdk.model;

import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ContractOfferDescriptionTest {

    @Test
    void verifyGetMethods() {

        Policy policy = Policy.Builder.newInstance().id("policyId").build();

        ContractOfferDescription contractOfferDescription = new ContractOfferDescription(
                "offerId", "assetId", "policyId", policy);

        assertAll(
                () -> assertThat(contractOfferDescription.getOfferId()).isEqualTo("offerId"),
                () -> assertThat(contractOfferDescription.getAssetId()).isEqualTo("assetId"),
                () -> assertThat(contractOfferDescription.getPolicyId()).isEqualTo("policyId"),
                () -> assertThat(contractOfferDescription.getPolicy().getUid()).isEqualTo("policyId")
        );
    }

}
