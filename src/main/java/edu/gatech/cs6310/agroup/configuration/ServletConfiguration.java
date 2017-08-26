package edu.gatech.cs6310.agroup.configuration;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mlarson on 4/19/16.
 */
@Configuration
public class ServletConfiguration {

    /*@Bean
    public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
        return new OpenEntityManagerInViewFilter();
    }*/

    /**
     * Create an OpenEntityManagerInView filter to avoid the lazy loading exceptions
     * @return
     */
    @Bean
    public FilterRegistrationBean configureOpenSessionInView() {
        List<String> servletPatterns = new ArrayList<>();
        servletPatterns.add("/*");

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new OpenEntityManagerInViewFilter());
        filterRegistrationBean.setUrlPatterns(servletPatterns);

        return filterRegistrationBean;
    }

    /*@Bean
    public org.springframework.orm.hibernate5.LocalSessionFactoryBean sessionFactory() {
        return new org.springframework.orm.hibernate5.LocalSessionFactoryBean();
    }*/
}
