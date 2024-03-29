package es.in2.desmos.infrastructure.broker.service.impl;

import es.in2.desmos.infrastructure.broker.model.BrokerSubscription;
import es.in2.desmos.infrastructure.broker.service.BrokerSubscriptionService;
import es.in2.desmos.infrastructure.broker.service.GenericBrokerService;
import es.in2.desmos.infrastructure.broker.util.BrokerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BrokerSubscriptionServiceImpl implements BrokerSubscriptionService {

    private final GenericBrokerService brokerAdapter;

    public BrokerSubscriptionServiceImpl(BrokerFactory brokerFactory) {
        this.brokerAdapter = brokerFactory.getBrokerAdapter();
    }

    @Override
    public Mono<Void> createSubscription(String processId, BrokerSubscription brokerSubscription) {
        return brokerAdapter.createSubscription(processId, brokerSubscription);
    }

}
