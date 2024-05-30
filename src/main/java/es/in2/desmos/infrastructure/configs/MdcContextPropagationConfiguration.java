package es.in2.desmos.infrastructure.configs;

import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ContextSnapshotFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

/**
 * 1. Will register ThreadLocalAccessors into ContextRegistry for fields listed in application.yml as property value
 * <b>management.tracing.baggage.correlation.fields</b>
 * 2. Enables Automatic Context Propagation for all reactive methods
 *
 * @see <a href="https://github.com/micrometer-metrics/context-propagation">context-propagation</a>
 */
@Slf4j
@Configuration
@ConditionalOnClass({ContextRegistry.class, ContextSnapshotFactory.class})
@ConditionalOnProperty(value = "management.tracing.baggage.correlation.fields", matchIfMissing = true)
public class MdcContextPropagationConfiguration {

    public MdcContextPropagationConfiguration(@Value("${management.tracing.baggage.correlation.fields}")
                                              List<String> fields) {

        Hooks.enableAutomaticContextPropagation();
        log.debug("Automatic Context Propagation enabled");

        if (!isEmpty(fields)) {
            fields.forEach(claim -> ContextRegistry.getInstance()
                    .registerThreadLocalAccessor(claim,
                            () -> MDC.get(claim),
                            value -> MDC.put(claim, value),
                            () -> MDC.remove(claim)));
        }

        log.debug("Registered fields on Thread Local Accessor");
    }
}
