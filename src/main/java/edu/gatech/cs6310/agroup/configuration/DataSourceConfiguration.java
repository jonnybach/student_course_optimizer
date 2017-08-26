package edu.gatech.cs6310.agroup.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * This class configures the primary data source connection
 *
 * @author matt.larson
 *
 */
@Configuration
public class DataSourceConfiguration {

    public static final String LOCAL_DATA_SOURCE_NAME = "dataSource";

    @Value("${project3.db.username}")
    private String username;

    @Value("${project3.db.password}")
    private String password;

    @Value("${project3.db.url}")
    private String url;

    @Primary
    @Bean(destroyMethod = "close", name = LOCAL_DATA_SOURCE_NAME)
    public DataSource dataSourceMysql() {

        //Configure the Tomcat connection pool
        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();

        //Configure MySQL
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setInitialSize(3);
        return ds;
    }
}