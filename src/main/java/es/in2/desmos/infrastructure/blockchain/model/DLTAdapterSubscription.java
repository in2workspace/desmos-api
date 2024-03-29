package es.in2.desmos.infrastructure.blockchain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record DLTAdapterSubscription(
        @JsonProperty("eventTypes") List<String> eventTypes,
        @JsonProperty("notificationEndpoint") String notificationEndpoint
) {
}
