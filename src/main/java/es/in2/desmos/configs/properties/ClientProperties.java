package es.in2.desmos.configs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration of the client which instantiates the solution.
 *
 * @param organizationId - Client OrganizationID information
 */
@ConfigurationProperties(prefix = "client")
public record ClientProperties(String organizationId) {
}
