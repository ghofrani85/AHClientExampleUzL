package eu.arrowhead;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.dto.shared.EventDTO;
import eu.arrowhead.misc.EventNotifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.concurrent.ConcurrentLinkedQueue;

@SpringBootApplication
@ComponentScan(basePackages = { CommonConstants.BASE_PACKAGE }) // TODO: add custom packages if any
public class SubscriberMain {
    public static void main(String[] args) {
        SpringApplication.run(SubscriberMain.class, args);
    }

    /**
     * Initialisierung Event Warteschlange fuer ankommende Events
     *
     * @return
     */
    @Bean(name = "Queue")
    public ConcurrentLinkedQueue<EventDTO> getNotificationQueue() {
        return new ConcurrentLinkedQueue<>();
    }

    /**
     * Initialisierung Task Thread
     *
     * @return
     */
    @Bean(name = "SUBSCRIBER_TASK")
    public EventNotifier getEventTask() {
        return new EventNotifier();
    }
}
