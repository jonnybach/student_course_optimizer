package edu.gatech.cs6310.agroup.util;

import edu.gatech.cs6310.agroup.model.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Created by tim on 3/24/16.
 */
@Component
public class MsgReceiver {

    private static final Logger log = LoggerFactory.getLogger(MsgReceiver.class);

    @Autowired
    ConfigurableApplicationContext context;

    //, containerFactory = "jmsListenerContainerFactory"
    @JmsListener(destination = "cs6310.project3.test.level")
    public void receiveMessage(String level) {
        log.info("Got message with level => " + level);
    }
}
