package net.catenax.edc.apiwrapper;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import net.catenax.edc.apiwrapper.cache.InMemoryEndpointDataReferenceCache;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.edr.EndpointDataReference;

@Consumes({MediaType.APPLICATION_JSON})
@Path("/endpoint-data-reference")
public class EdcCallbackController {

    private final Monitor monitor;
    private final InMemoryEndpointDataReferenceCache endpointDataReferenceCache;

    public EdcCallbackController(Monitor monitor, InMemoryEndpointDataReferenceCache endpointDataReferenceCache) {
        this.monitor = monitor;
        this.endpointDataReferenceCache = endpointDataReferenceCache;
    }

    @POST
    public void receiveEdcCallback(EndpointDataReference dataReference) {
        var contractAgreementId = dataReference.getProperties().get("cid");
        endpointDataReferenceCache.put(contractAgreementId, dataReference);
        monitor.debug("Endpoint Data Reference received and cached for agreement: " + contractAgreementId);
    }
}
