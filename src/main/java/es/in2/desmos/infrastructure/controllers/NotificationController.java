package es.in2.desmos.infrastructure.controllers;

import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.models.BrokerNotification;
import es.in2.desmos.domain.services.blockchain.BlockchainListenerService;
import es.in2.desmos.domain.services.broker.BrokerListenerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    public static final String PROCESS_ID_MDC_KEY = "processId";
    private final BrokerListenerService brokerListenerService;
    private final BlockchainListenerService blockchainListenerService;

    @PostMapping("/broker")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> postBrokerNotification(@RequestBody @Valid BrokerNotification brokerNotification) {
        String processId = UUID.randomUUID().toString();
        MDC.put(PROCESS_ID_MDC_KEY, processId);

        log.info("Broker Notification received");
        log.debug("Broker Notification received: {}", brokerNotification.toString());

        return brokerListenerService.processBrokerNotification(processId, brokerNotification)
                .contextWrite(Context.of(PROCESS_ID_MDC_KEY, processId));
    }

    @PostMapping("/dlt")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> postDLTNotification(@RequestBody @Valid BlockchainNotification blockchainNotification) {
        String processId = UUID.randomUUID().toString();
        MDC.put(PROCESS_ID_MDC_KEY, processId);

        log.info("Blockchain Notification received");
        log.debug(" Blockchain Notification received: {}", blockchainNotification);

        return blockchainListenerService.processBlockchainNotification(processId, blockchainNotification)
                .contextWrite(Context.of(PROCESS_ID_MDC_KEY, processId));
    }

}
