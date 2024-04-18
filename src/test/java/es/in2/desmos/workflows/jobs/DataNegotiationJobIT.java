package es.in2.desmos.workflows.jobs;

import es.in2.desmos.ContainerManager;
import es.in2.desmos.domain.events.DataNegotiationEventPublisher;
import es.in2.desmos.objectmothers.DataNegotiationEventMother;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext
class DataNegotiationJobIT {
    @Autowired
    private DataNegotiationEventPublisher dataNegotiationEventPublisher;

    @SpyBean
    private DataNegotiationJob dataNegotiationJob;

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    @Test
    void itShouldBeListenWhenEventIsCalled(){
        dataNegotiationEventPublisher.publishEvent(DataNegotiationEventMother.empty());

        verify(dataNegotiationJob, times(1)).negotiateDataSync(any());
        verifyNoMoreInteractions(dataNegotiationJob);
    }
}