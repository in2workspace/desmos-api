package es.in2.desmos.application.runners;

import es.in2.desmos.application.workflows.DataSyncWorkflow;
import es.in2.desmos.application.workflows.PublishWorkflow;
import es.in2.desmos.application.workflows.SubscribeWorkflow;
import es.in2.desmos.domain.exceptions.RequestErrorException;
import es.in2.desmos.domain.models.BlockchainSubscription;
import es.in2.desmos.domain.models.BrokerSubscription;
import es.in2.desmos.domain.services.blockchain.BlockchainListenerService;
import es.in2.desmos.domain.services.broker.BrokerListenerService;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import es.in2.desmos.infrastructure.configs.BlockchainConfig;
import es.in2.desmos.infrastructure.configs.BrokerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static es.in2.desmos.domain.utils.ApplicationConstants.SUBSCRIPTION_ID_PREFIX;
import static es.in2.desmos.domain.utils.ApplicationConstants.SUBSCRIPTION_TYPE;
import static es.in2.desmos.domain.utils.ApplicationUtils.getEnvironmentMetadata;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationRunner {

    private final ApiConfig apiConfig;
    private final BrokerConfig brokerConfig;
    private final BlockchainConfig blockchainConfig;
    private final BrokerListenerService brokerListenerService;
    private final BlockchainListenerService blockchainListenerService;
    private final DataSyncWorkflow dataSyncWorkflow;
    private final PublishWorkflow publishWorkflow;
    private final SubscribeWorkflow subscribeWorkflow;
    private final AtomicBoolean isQueueAuthorizedForEmit = new AtomicBoolean(false);
    private final String getCurrentEnvironment;
    private Disposable publishQueueDisposable;
    private Disposable subscribeQueueDisposable;

    @EventListener(ApplicationReadyEvent.class)
    public Mono<Void> onApplicationReady() {
        String processId = UUID.randomUUID().toString();
        MDC.put("processId", processId);

        log.info("Setting initial configurations...");

        return setBrokerSubscription(processId)
                .then(setBlockchainSubscription(processId))
                .thenMany(initializeDataSync(processId))
                .then()
                .contextWrite(Context.of("processId", processId));
    }

    @Retryable(retryFor = RequestErrorException.class, maxAttempts = 4, backoff = @Backoff(delay = 2000))
    private Mono<Void> setBrokerSubscription(String processId) {
        log.info("Setting Broker Subscription...");
        // Build Entity Type List to subscribe to
        List<BrokerSubscription.Entity> entities = new ArrayList<>();
        brokerConfig.getEntityTypes().forEach(entityType -> entities.add(BrokerSubscription.Entity.builder().type(entityType).build()));
        // Create the Broker Subscription object
        BrokerSubscription brokerSubscription = BrokerSubscription.builder()
                .id(SUBSCRIPTION_ID_PREFIX + UUID.randomUUID())
                .type(SUBSCRIPTION_TYPE)
                .entities(entities)
                .notification(BrokerSubscription.SubscriptionNotification.builder()
                        .subscriptionEndpoint(BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.builder()
                                .uri(brokerConfig.getNotificationEndpoint())
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .receiverInfo(List.of(BrokerSubscription.SubscriptionNotification.SubscriptionEndpoint.RetrievalInfoContentType.builder()
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .build()))
                                .build())
                        .build())
                .build();
        // Create the subscription and log the result
        log.debug("Broker Subscription: {}", brokerSubscription);
        return brokerListenerService.createSubscription(processId, brokerSubscription)
                .doOnSuccess(response -> log.info("Broker Subscription created successfully."))
                .doOnError(e -> log.error("Error creating Broker Subscription", e));
    }

    @Retryable(retryFor = RequestErrorException.class, maxAttempts = 4, backoff = @Backoff(delay = 2000))
    private Mono<Void> setBlockchainSubscription(String processId) {
        log.info("Setting Blockchain Subscription...");
        // Create the Blockchain Subscription object
        BlockchainSubscription blockchainSubscription = BlockchainSubscription.builder()
                .eventTypes(blockchainConfig.getEntityTypes())
                .metadata(List.of(getEnvironmentMetadata(apiConfig.getCurrentEnvironment())))
                .notificationEndpoint(blockchainConfig.getNotificationEndpoint())
                .build();
        // Create the subscription
        return blockchainListenerService.createSubscription(processId, blockchainSubscription)
                .doOnSuccess(response -> log.info("Blockchain Subscription created successfully."))
                .doOnError(e -> log.error("Error creating Blockchain Subscription", e));
    }

    private Flux<Void> initializeDataSync(String processId) {
        log.info("Initializing Data Synchronization Workflow...");
        // Start data synchronization process
        return dataSyncWorkflow.startDataSyncWorkflow(processId)
                .doOnTerminate(() -> {
                    log.info("Data Synchronization Workflow has finished.");
                    log.info("Authorizing queues for Pub-Sub Workflows...");
                    isQueueAuthorizedForEmit.set(true);
                    initializeQueueProcessing(processId);
                    log.info("Queues have been authorized and enabled.");
                });
    }

    private void initializeQueueProcessing(String processId) {
        if (!isQueueProcessingAuthorized()) {
            log.debug("Queue processing is currently paused.");
            return;
        }
        log.debug("Starting queue processing...");
        restartQueueProcessing(processId);
    }

    private boolean isQueueProcessingAuthorized() {
        return isQueueAuthorizedForEmit.get();
    }

    private void restartQueueProcessing(String processId) {
        log.debug("Restarting queue processing...");
        resetActiveSubscriptions(processId);
        startBlockchainEventProcessing(processId);
        startBrokerEventProcessing(processId);
    }

    private void resetActiveSubscriptions(String processId) {
        log.debug("Resetting active subscriptions...");
        disposeIfActive(publishQueueDisposable);
        disposeIfActive(subscribeQueueDisposable);
    }

    private void disposeIfActive(Disposable subscription) {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

    private void startBlockchainEventProcessing(String processId) {
        publishQueueDisposable = publishWorkflow.startPublishWorkflow(processId)
                .subscribe(
                        null,
                        error -> log.error("Error occurred during Publish Workflow", error),
                        () -> log.info("Publish Workflow completed")
                );
    }

    private void startBrokerEventProcessing(String processId) {
        subscribeQueueDisposable = subscribeWorkflow.startSubscribeWorkflow(processId)
                .subscribe(
                        null,
                        error -> log.error("Error occurred during Subscribe Workflow", error),
                        () -> log.info("Subscribe Workflow completed")
                );
    }

    // TODO: Implement the recover method
    @Recover
    public void recover(RequestErrorException e) {
        log.error("After retries, subscription failed", e);
    }

}