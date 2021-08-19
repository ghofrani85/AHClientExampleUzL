package eu.arrowhead.controller;

import eu.arrowhead.client.skeleton.common.eventhandler.EventHandler;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.dto.shared.EventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * RestController for Events
 *
 * each published event will be a POST request to each subscriber
 */
@RestController
public class EventController {
    @Resource(name = "Queue")
    private ConcurrentLinkedQueue<EventDTO> notificationQueue;

    @Autowired
    private EventHandler eventHandler;

    /**
     * Echo
     * @return
     */
    @GetMapping(path = CommonConstants.ECHO_URI)
    public ResponseEntity echoService() {
        return new ResponseEntity("Got it!", HttpStatus.OK);
    }

    /**
     * dynamic endpoint for each event
     * @param endpoint
     * @param eventDTO
     * @return
     */
    @PostMapping(path = "/{endpoint}")
    public ResponseEntity dynamicEventEndpoint(@PathVariable("endpoint") String endpoint,
                                               @RequestBody EventDTO eventDTO) {
        if (!eventHandler.getConfigEventProperties(false).containsValue(endpoint))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        notificationQueue.add(eventDTO);

        return new ResponseEntity(HttpStatus.OK);
    }
}
