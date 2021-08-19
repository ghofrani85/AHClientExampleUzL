package eu.arrowhead.client.skeleton.common.eventhandler;

import java.util.Map;
import eu.arrowhead.client.skeleton.common.model.Events;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * mapping events from application.properties
 * basic path: arrowhead.client.publisher.events.x=y
 * basic path: arrowhead.client.subscriber.events.x=y
 */
@Configuration
@PropertySource("classpath:application.properties")
public class ConfigEventProperties {
	@Bean
	@ConfigurationProperties(prefix = "arrowhead.client.publisher.events")
	public Map<String, String> publisherEvents() {
		return new Events().getEvents();
	}

	@Bean
	@ConfigurationProperties(prefix = "arrowhead.client.subscriber.events")
	public Map<String, String> subscriberEvents() {
		return new Events().getEvents();
	}
}
