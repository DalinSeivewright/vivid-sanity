package ca.logichromatic.vividsanity.configuration;

import ca.logichromatic.vividsanity.filters.ImageRouterFilter;

import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableZuulServer
public class ApplicationConfiguration {

    @Bean
    public ImageRouterFilter imageRouterFilter() {
        return new ImageRouterFilter();
    }

//    @Bean
//    @ConditionalOnMissingBean(Test.class)
//    public Test test() {
//        return new Test();
//    }


}


