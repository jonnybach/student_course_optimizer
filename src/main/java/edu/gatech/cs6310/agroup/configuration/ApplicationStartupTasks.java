package edu.gatech.cs6310.agroup.configuration;

/**
 * Created by ubuntu on 3/24/16. This will handle initial loading of resources when the app starts up
 *
 */

import edu.gatech.cs6310.agroup.exception.EventSerializationException;
import edu.gatech.cs6310.agroup.service.StaticLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.annotation.PostConstruct;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
public class ApplicationStartupTasks {

    @Autowired
    StaticLoaderService staticLoaderService;

    @Autowired
    JmsMessagingTemplate jms;

    Logger logger = LoggerFactory.getLogger(ApplicationStartupTasks.class);

    @PostConstruct
    public void setupResources() {

        logger.debug("Loading static resources");
        staticLoaderService.loadResourcesIfEmpty();

        try {
            staticLoaderService.prePopulateEventLogCoursesIfEmpty();
        } catch (EventSerializationException e) {
            logger.error("Could not pre-populate EventLog", e);
        }

        logger.debug("Initializing system settings");

    }

}