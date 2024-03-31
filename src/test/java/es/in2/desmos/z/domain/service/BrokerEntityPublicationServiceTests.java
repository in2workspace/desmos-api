//package es.in2.desmos.todo.domain.service;
//
//import es.in2.desmos.domain.models.BlockchainNotification;
//import es.in2.desmos.domain.models.FailedEntityTransaction;
//import es.in2.desmos.domain.services.api.impl.BrokerEntityPublisherServiceImpl;
//import es.in2.desmos.domain.utils.ApplicationUtils;
//import es.in2.desmos.z.services.BrokerPublicationService;
//import es.in2.desmos.z.services.TransactionService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.security.NoSuchAlgorithmException;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class BrokerEntityPublicationServiceTests {
//
//    String processId;
//    String entityId;
//    BlockchainNotification notification;
//    @Mock
//    private TransactionService transactionService;
//    @Mock
//    private BrokerPublicationService brokerPublicationService;
//    @InjectMocks
//    private BrokerEntityPublisherServiceImpl brokerEntityPublicationService;
//
//    @BeforeEach
//    void setUp() {
//        processId = "process123";
//        entityId = "entity123";
//        notification = BlockchainNotification.builder()
//                .dataLocation("http://broker.internal/entities/entity123")
//                .previousEntityHash("previousHash")
//                .build();
//    }
//
//
//    @Test
//    void testDeletedEntityNotification() {
//        // Arrange
//        when(brokerPublicationService.deleteEntityById(processId, entityId)).thenReturn(Mono.empty());
//        when(transactionService.saveTransaction(any(), any())).thenReturn(Mono.empty());
//        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
//            applicationUtils
//                    .when(() -> extractEntityIdFromDataLocation(anyString()))
//                    .thenReturn(entityId);
//            // Act & Assert
//            StepVerifier.create(
//                            brokerEntityPublicationService.publishRetrievedEntityToBroker(processId, "{errorCode: 404}", notification))
//                    .verifyComplete();
//        }
//    }
//
//    @Test
//    void testNotDeletedEntityNotification() {
//        // Arrange
//        String retrievedBrokerEntity = "brokerEntity";
//        BlockchainNotification mockBlockchainNotification = mock(BlockchainNotification.class);
//        when(mockBlockchainNotification.previousEntityHash()).thenReturn("0x0000000000000000000000000000000000000000000000000000000000000000");
//        when(mockBlockchainNotification.dataLocation()).thenReturn("http://broker.internal/entities/entity123");
//        when(brokerPublicationService.getEntityById(processId, entityId)).thenReturn(Mono.just("{errorCode: 404}"));
//        when(brokerPublicationService.postEntity(processId, retrievedBrokerEntity)).thenReturn(Mono.empty());
//        when(transactionService.saveTransaction(any(), any())).thenReturn(Mono.empty());
//        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
//            applicationUtils
//                    .when(() -> extractEntityIdFromDataLocation(anyString()))
//                    .thenReturn(entityId);
//            applicationUtils
//                    .when(() -> calculateSHA256(anyString()))
//                    .thenReturn(entityId);
//            applicationUtils
//                    .when(() -> extractHashLinkFromDataLocation(anyString()))
//                    .thenReturn(entityId);
//            // Act & Assert
//            StepVerifier.create(
//                            brokerEntityPublicationService.publishRetrievedEntityToBroker(processId, retrievedBrokerEntity,
//                                    mockBlockchainNotification))
//                    .verifyComplete();
//        }
//    }
//
//    @Test
//    void testValidEntityIntegrity() {
//        // Arrange
//        String retrievedBrokerEntity = "brokerEntity";
//        BlockchainNotification mockBlockchainNotification = mock(BlockchainNotification.class);
//        when(mockBlockchainNotification.previousEntityHash()).thenReturn("0x0000000000000000000000000000000000000000000000000000000000000000");
//        when(mockBlockchainNotification.dataLocation()).thenReturn("http://broker.internal/entities/entity123");
//        when(brokerPublicationService.getEntityById(processId, entityId)).thenReturn(Mono.just("Ok"));
//        when(brokerPublicationService.updateEntity(processId, retrievedBrokerEntity)).thenReturn(Mono.empty());
//        when(transactionService.saveTransaction(any(), any())).thenReturn(Mono.empty());
//        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
//            applicationUtils
//                    .when(() -> extractEntityIdFromDataLocation(anyString()))
//                    .thenReturn(entityId);
//            applicationUtils
//                    .when(() -> calculateSHA256(anyString()))
//                    .thenReturn(entityId);
//            applicationUtils
//                    .when(() -> extractHashLinkFromDataLocation(anyString()))
//                    .thenReturn(entityId);
//            // Act & Assert
//            StepVerifier.create(
//                            brokerEntityPublicationService.publishRetrievedEntityToBroker(processId, retrievedBrokerEntity,
//                                    mockBlockchainNotification))
//                    .verifyComplete();
//        }
//    }
//
//    @Test
//    void testInvalidEntityIntegrity() {
//        // Arrange
//        String retrievedBrokerEntity = "brokerEntity";
//
//        BlockchainNotification mockBlockchainNotification = mock(BlockchainNotification.class);
//        when(mockBlockchainNotification.previousEntityHash()).thenReturn("0x0000000000000000000000000000000000000000000000000000000000000000");
//        when(mockBlockchainNotification.dataLocation()).thenReturn("http://broker.internal/entities/entity123");
//
//        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
//            applicationUtils
//                    .when(() -> extractEntityIdFromDataLocation(anyString()))
//                    .thenReturn(entityId);
//            applicationUtils
//                    .when(() -> calculateSHA256(anyString()))
//                    .thenReturn("entity1");
//            applicationUtils
//                    .when(() -> extractHashLinkFromDataLocation(anyString()))
//                    .thenReturn("entity2");
//            // Act & Assert
//            StepVerifier.create(
//                            brokerEntityPublicationService.publishRetrievedEntityToBroker(processId, retrievedBrokerEntity,
//                                    mockBlockchainNotification))
//                    .verifyError(IllegalArgumentException.class);
//        }
//    }
//
//    @Test
//    void testCatchException() {
//        // Arrange
//        String retrievedBrokerEntity = "brokerEntity";
//        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
//            applicationUtils
//                    .when(() -> extractEntityIdFromDataLocation(anyString()))
//                    .thenReturn(entityId);
//            applicationUtils
//                    .when(() -> calculateSHA256(anyString()))
//                    .thenThrow(NoSuchAlgorithmException.class);
//            // Act & Assert
//            StepVerifier.create(
//                            brokerEntityPublicationService.publishRetrievedEntityToBroker(processId, retrievedBrokerEntity, notification))
//                    .verifyError(NoSuchAlgorithmException.class);
//        }
//    }
//
//    @Test
//    void testDeletedEntityNotificationWithError() {
//        // Arrange
//        String errorCodeJson = "{errorCode: 404}";
//        when(brokerPublicationService.deleteEntityById(processId, entityId))
//                .thenReturn(Mono.error(new RuntimeException("Simulated deletion error")));
//        when(transactionService.saveFailedEntityTransaction(anyString(), any(FailedEntityTransaction.class)))
//                .thenReturn(Mono.empty());
//        when(transactionService.saveTransaction(any(), any())).thenReturn(Mono.empty());
//
//
//        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
//            applicationUtils.when(() -> extractEntityIdFromDataLocation(anyString())).thenReturn(entityId);
//
//            // Act & Assert
//            StepVerifier.create(
//                            brokerEntityPublicationService.publishRetrievedEntityToBroker(processId, errorCodeJson, notification))
//                    .expectComplete()
//                    .verify();
//
//            Mockito.verify(transactionService).saveFailedEntityTransaction(anyString(), any(FailedEntityTransaction.class));
//        }
//    }
//
//    @Test
//    void testValidEntityIntegrityRecover() {
//        // Arrange
//        String retrievedBrokerEntity = "brokerEntity";
//        BlockchainNotification mockBlockchainNotification = mock(BlockchainNotification.class);
//        when(mockBlockchainNotification.previousEntityHash()).thenReturn("0x0000000000000000000000000000000000000000000000000000000000000000");
//        when(mockBlockchainNotification.dataLocation()).thenReturn("http://broker.internal/entities/entity123");
//        when(brokerPublicationService.getEntityById(processId, entityId)).thenReturn(Mono.just("Ok"));
//        when(brokerPublicationService.updateEntity(processId, retrievedBrokerEntity)).thenReturn(Mono.error(new RuntimeException("Simulated deletion error")));
//        when(transactionService.saveFailedEntityTransaction(anyString(), any(FailedEntityTransaction.class)))
//                .thenReturn(Mono.empty());
//        when(transactionService.saveTransaction(any(), any())).thenReturn(Mono.empty());
//
//        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
//            applicationUtils
//                    .when(() -> extractEntityIdFromDataLocation(anyString()))
//                    .thenReturn(entityId);
//            applicationUtils
//                    .when(() -> calculateSHA256(anyString()))
//                    .thenReturn(entityId);
//            applicationUtils
//                    .when(() -> extractHashLinkFromDataLocation(anyString()))
//                    .thenReturn(entityId);
//            // Act & Assert
//            StepVerifier.create(
//                            brokerEntityPublicationService.publishRetrievedEntityToBroker(processId, retrievedBrokerEntity,
//                                    mockBlockchainNotification))
//                    .verifyComplete();
//
//
//        }
//
//    }
//    @Test
//    void testNewValidEntityIntegrityRecover() {
//        // Arrange
//        String retrievedBrokerEntity = "brokerEntity";
//        BlockchainNotification mockBlockchainNotification = mock(BlockchainNotification.class);
//        when(mockBlockchainNotification.previousEntityHash()).thenReturn("0x0000000000000000000000000000000000000000000000000000000000000000");
//        when(mockBlockchainNotification.dataLocation()).thenReturn("http://broker.internal/entities/entity123");
//        when(brokerPublicationService.getEntityById(processId, entityId)).thenReturn(Mono.just("{errorCode: 404}"));
//        when(brokerPublicationService.postEntity(processId, retrievedBrokerEntity)).thenReturn(Mono.error(new RuntimeException("Simulated deletion error")));
//        when(transactionService.saveFailedEntityTransaction(anyString(), any(FailedEntityTransaction.class)))
//                .thenReturn(Mono.empty());
//        when(transactionService.saveTransaction(any(), any())).thenReturn(Mono.empty());
//
//        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
//            applicationUtils
//                    .when(() -> extractEntityIdFromDataLocation(anyString()))
//                    .thenReturn(entityId);
//            applicationUtils
//                    .when(() -> calculateSHA256(anyString()))
//                    .thenReturn(entityId);
//            applicationUtils
//                    .when(() -> extractHashLinkFromDataLocation(anyString()))
//                    .thenReturn(entityId);
//            // Act & Assert
//            StepVerifier.create(
//                            brokerEntityPublicationService.publishRetrievedEntityToBroker(processId, retrievedBrokerEntity,
//                                    mockBlockchainNotification))
//                    .verifyComplete();
//
//
//        }
//
//    }
//
//}
