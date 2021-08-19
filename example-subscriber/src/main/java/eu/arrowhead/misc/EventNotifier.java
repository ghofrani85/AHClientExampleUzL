package eu.arrowhead.misc;

import eu.arrowhead.client.skeleton.common.eventhandler.EventHandler;
import eu.arrowhead.client.skeleton.common.orchestrator.Orchestrator;
import eu.arrowhead.common.dto.shared.EventDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.SystemResponseDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventNotifier extends Thread {
    private boolean interrupted = false;
    private final Logger logger = LogManager.getLogger(EventNotifier.class);

    @Resource(name = "Queue")
    private ConcurrentLinkedQueue<EventDTO> notificationQueue;

    @Autowired
    private Orchestrator orchestrator;

    @Autowired
    private EventHandler eventHandler;

    //-------------------------------------------------------------------------------------------------

    /**
     * print all events to console
     */
    @Override
    public void run() {
        final Set<SystemResponseDTO> sources = new HashSet<>();
        List<OrchestrationResultDTO> testprovider = null;
        Map<String, String> metadata = new HashMap<>();

        interrupted = Thread.currentThread().isInterrupted();


        // set general service metadata
        metadata.put("country", "Germany");
        metadata.put("location", "Dresden");
        metadata.put("organization", "HTW Dresden");
        metadata.put("department", "industrie40");

        // receive publisher service
        try {
            testprovider = orchestrator.orchestrate("publish-data", "HTTP-INSECURE-JSON", HttpMethod.GET, metadata);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // end thread if no service could find
        if (testprovider.isEmpty())
            Thread.currentThread().interrupt();

        // subscribe to service
        sources.add(testprovider.get(0).getProvider());
        eventHandler.subscribe(sources);

        // output event notifications
        while (!interrupted) {
            try {
                if (notificationQueue.peek() != null) {
                    for (final EventDTO event : notificationQueue)
                        System.out.println("Event: " + event.getEventType() + ", Payload: " + event.getPayload());

                    notificationQueue.clear();
                }
            } catch (final Throwable ex) {
                logger.info(ex.getMessage());
            }
        }
    }

    //-------------------------------------------------------------------------------------------------
    /**
     * clean destroy of the service
     */
    public void destroy() {
        logger.debug("ConsumerTask.destroy started...");
        interrupted = true;
    }
}
