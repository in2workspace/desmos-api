package es.in2.desmos.services.blockchain.adapter;

import es.in2.desmos.domain.models.BlockchainSubscription;
import es.in2.desmos.domain.models.BlockchainTxPayload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BlockchainAdapterService {
    Mono<Void> createSubscription(String processId, BlockchainSubscription blockchainSubscription);
    Mono<Void> publishEvent(String processId, BlockchainTxPayload blockchainTxPayload);
    Flux<String> getEventsFromRange(String processId, long from, long to);
}

