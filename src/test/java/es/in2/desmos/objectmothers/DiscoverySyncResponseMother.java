package es.in2.desmos.objectmothers;

import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class DiscoverySyncResponseMother {
    private DiscoverySyncResponseMother() {
    }

    public static @NotNull DiscoverySyncResponse simpleDiscoverySyncResponse(String contextBrokerExternalDomain) {
        List<ProductOffering> productOfferingList = new ArrayList<>();
        productOfferingList.add(ProductOfferingMother.sample3());
        productOfferingList.add(ProductOfferingMother.sample4());
        return new DiscoverySyncResponse(contextBrokerExternalDomain, productOfferingList);
    }

    public static @NotNull DiscoverySyncResponse fullDiscoverySyncResponse(String contextBrokerExternalDomain) {
        List<ProductOffering> productOfferingList = ProductOfferingMother.fullList();
        return new DiscoverySyncResponse(contextBrokerExternalDomain, productOfferingList);
    }
}
