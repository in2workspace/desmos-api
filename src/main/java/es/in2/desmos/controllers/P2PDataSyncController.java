package es.in2.desmos.controllers;

import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import es.in2.desmos.domain.models.Entity;
import es.in2.desmos.workflows.DiscoverySyncWorkflow;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/sync/p2p")
@RequiredArgsConstructor
public class P2PDataSyncController {
    private final DiscoverySyncWorkflow discoverySyncWorkflow;

    @Value("${broker.externalDomain}")
    private String contextBrokerExternalDomain;

    @PostMapping("/discovery")
    @ResponseStatus(HttpStatus.OK)
    public Mono<DiscoverySyncResponse> discoverySync(@RequestBody @Valid Mono<DiscoverySyncRequest> discoverySyncRequest) {
        String processId = UUID.randomUUID().toString();
        log.info("ProcessID: {} - Starting Synchronization Discovery...", processId);

        Mono<String> issuer = discoverySyncRequest.map(DiscoverySyncRequest::issuer);
        Mono<List<String>> externalEntityIds = discoverySyncRequest.map(DiscoverySyncRequest::createExternalEntityIdsStringList);

        Mono<List<Entity>> localEntityIds = discoverySyncWorkflow.discoverySync(processId, issuer, externalEntityIds);

        return localEntityIds.map(productOfferings -> new DiscoverySyncResponse(contextBrokerExternalDomain, productOfferings));
    }
}