package edu.gatech.cs6310.agroup.configuration;

/**
 * Created by ubuntu on 3/24/16.
 */

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DbMigrationConfiguration {

    /**
     * Liquibase is configured here
     *
     * @return the bean that holds the Liquibase configuration
     */
    @Bean(name="liquibase")
    public SpringLiquibase configureLiquibase(@Qualifier(DataSourceConfiguration.LOCAL_DATA_SOURCE_NAME) DataSource dataSource) {
        SpringLiquibase liquibaseSettings = new SpringLiquibase();
        liquibaseSettings.setChangeLog("classpath:/db/changelog-master.xml");
        liquibaseSettings.setDataSource(dataSource);

        return liquibaseSettings;
    }
}