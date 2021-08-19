package eu.arrowhead.client.skeleton.common.serviceregistry.standalone;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

/**
 * mapping systems/services from application.properties
 * mapping systems: arrowhead.client.register.{SYSTEM}
 * mapping services incl. metadata: arrowhead.client.register.{SYSTEM}.services.{SERVICE}
 */
@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "arrowhead.client.register")
public class SystemDefinition {
    private Map<String, SystemDefinitionData> systems;

    public Map<String, SystemDefinitionData> getSystems() {
        return systems;
    }

    public void setSystems(Map<String, SystemDefinitionData> systems) {
        this.systems = systems;
    }
}
