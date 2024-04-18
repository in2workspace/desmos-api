package es.in2.desmos.workflows;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import reactor.core.publisher.Mono;

import java.util.List;

public interface P2PDataSyncWorkflow {
    Mono<List<MVEntity4DataNegotiation>> dataDiscovery(String processId, Mono<String> issuer, Mono<List<MVEntity4DataNegotiation>> externalMvEntities4DataNegotiation);
}