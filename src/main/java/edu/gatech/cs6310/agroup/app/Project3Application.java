package edu.gatech.cs6310.agroup.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("edu.gatech.cs6310.agroup") //This picks up components/services/beans/configurations
@EnableJpaRepositories("edu.gatech.cs6310.agroup.repository") //Adds the JPA repository interface classes
@EntityScan("edu.gatech.cs6310.agroup.model") //This is the package where the JPA entities live
@EnableJms //Enable JMS support
@EnableScheduling
@EnableCaching
public class Project3Application {

    private static final Logger log = LoggerFactory.getLogger(Project3Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Project3Application.class, args);
    }
}
