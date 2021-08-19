package eu.arrowhead.controller;

import eu.arrowhead.client.skeleton.common.eventhandler.EventHandler;
import eu.arrowhead.common.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping(path = "/api")
public class TestController {
    @Autowired
    private EventHandler eventHandler;

    @GetMapping(path = CommonConstants.ECHO_URI)
    public ResponseEntity echoService() {
        eventHandler.publish(eventHandler.getEvent("request-received", true), new HashMap<>(), null);
        return new ResponseEntity(HttpStatus.OK);
    }
}
