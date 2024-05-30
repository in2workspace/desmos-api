package es.in2.desmos.infrastructure.controllers;

import es.in2.desmos.domain.services.sync.services.DataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/sync/data")
@RequiredArgsConstructor
public class DataSyncController {

    private final DataSyncService dataSyncService;

    @GetMapping
    public Mono<Void> syncData() {
        String processId = UUID.randomUUID().toString();
        MDC.put("processId", processId);

        log.info("Starting Data Synchronization...");

        return dataSyncService.synchronizeData(processId)
                .contextWrite(Context.of("processId", processId));
    }

}
