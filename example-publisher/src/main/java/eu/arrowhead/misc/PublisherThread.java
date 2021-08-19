package eu.arrowhead.misc;

import eu.arrowhead.client.skeleton.common.eventhandler.EventHandler;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 *  sends events at irregular intervals
 *  Time interval between 1 and 10 seconds and is rolled out after each publish
 */
@Service
public class PublisherThread implements Runnable {
    private boolean isInterrupted = false;
    private EventHandler eventHandler;

    public PublisherThread(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public void run() {
        while (!isInterrupted) {
            eventHandler.publish(eventHandler.getEvent("new-data", true), new HashMap<>(),
                    RandomStringUtils.random(10,true, true));

            try {
                Thread.sleep(RandomUtils.nextInt(1, 10) * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        eventHandler.publish(eventHandler.getEvent("destroyed", true), new HashMap<>(), null);
    }

    /**
     * interrupt thread
     */
    public void interrupt() {
        this.isInterrupted = true;
    }
}
