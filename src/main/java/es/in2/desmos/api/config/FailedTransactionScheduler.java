package es.in2.desmos.api.config;

import es.in2.desmos.api.model.BlockchainEvent;
import es.in2.desmos.api.model.BlockchainNotification;
import es.in2.desmos.api.model.EventQueue;
import es.in2.desmos.api.service.QueueService;
import es.in2.desmos.api.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class FailedTransactionScheduler {

    private final QueueService brokerToBlockchainQueueService;
    private final QueueService blockchainToBrokerQueueService;
    private final TransactionService transactionService;


    @Scheduled(fixedRate = 60000)
    public void processFailedEvents() {
        String processId = UUID.randomUUID().toString();
        MDC.put("processId", processId);
        log.debug("Retrying failed events...");

        transactionService.getAllFailedEventTransactions(processId)
                .flatMap(event ->
                        transactionService.deleteFailedEventTransaction(processId, event.getId())
                                .then(Mono.defer(() -> {
                                    BlockchainEvent blkEvent = BlockchainEvent.builder()
                                            .eventType(event.getEntityType())
                                            .dataLocation(event.getDatalocation())
                                            .entityId(event.getEntityId())
                                            .previousEntityHash(event.getPreviousEntityHash())
                                            .organizationId(event.getOrganizationId())
                                            .build();
                                    return brokerToBlockchainQueueService.enqueueEvent(EventQueue.builder()
                                            .event(Collections.singletonList(blkEvent))
                                            .priority(event.getPriority())
                                            .build());
                                }))
                ).switchIfEmpty(
                        Mono.defer(() -> {
                            log.debug("No failed events to process");
                            return Mono.empty();
                        })
                )
                .subscribe(
                        success -> log.debug("Event retried successfully"),
                        error -> log.debug("Error retrying event")
                );
    }

    @Scheduled(fixedRate = 60000)
    public void processFailedEntities() {
        String processId = UUID.randomUUID().toString();
        MDC.put("processId", processId);
        log.debug("Retrying failed entities...");

        transactionService.getAllFailedEntityTransactions(processId)
                .flatMap(entity ->
                        transactionService.deleteFailedEntityTransaction(processId, entity.getId())
                                .then(Mono.defer(() -> {
                                    BlockchainNotification blkNotification = BlockchainNotification.builder()
                                            .id(entity.getNotificationId())
                                            .dataLocation(entity.getDatalocation())
                                            .timestamp(entity.getTimestamp())
                                            .eventType(entity.getEntityType())
                                            .entityId(entity.getEntityId())
                                            .previousEntityHash(entity.getPreviousEntityHash())
                                            .build();
                                    return blockchainToBrokerQueueService.enqueueEvent(EventQueue.builder()
                                            .event(List.of(blkNotification, entity.getEntity()))
                                            .priority(entity.getPriority())
                                            .build());
                                }))
                ).switchIfEmpty(
                        Mono.defer(() -> {
                            log.debug("No failed entities to process");
                            return Mono.empty();
                        })
                )
                .subscribe(
                        success -> log.debug("Entity retried successfully"),
                        error -> log.debug("Error retrying entity")
                );
    }
}