package es.in2.desmos.domain.services.broker;

import es.in2.desmos.domain.services.broker.adapter.BrokerAdapterService;
import es.in2.desmos.domain.services.broker.adapter.factory.BrokerAdapterFactory;
import es.in2.desmos.domain.services.broker.impl.BrokerPublisherServiceImpl;
import es.in2.desmos.objectmothers.EntitySyncResponseMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrokerPublisherServiceTests {
    @Mock
    private BrokerAdapterFactory brokerAdapterFactory;

    @Mock
    private BrokerAdapterService brokerAdapterService;

    private BrokerPublisherServiceImpl brokerPublisherService;

    @BeforeEach
    void init() {
        when(brokerAdapterFactory.getBrokerAdapter()).thenReturn(brokerAdapterService);
        brokerPublisherService = new BrokerPublisherServiceImpl(brokerAdapterFactory);
    }

    @Test
    void itShouldPublishNewBatchDataToBroker() {
        String processId = "0";

        String retrievedBrokerEntities = EntitySyncResponseMother.sample();

        when(brokerAdapterService.batchPostEntities(processId, retrievedBrokerEntities)).thenReturn(Mono.empty());

        Mono<Void> result = brokerPublisherService.publishNewBatchDataToBroker(processId, retrievedBrokerEntities);

        StepVerifier
                .create(result)
                .verifyComplete();

        verify(brokerAdapterService, times(1)).batchPostEntities(processId, retrievedBrokerEntities);
        verifyNoMoreInteractions(brokerAdapterService);
    }
}