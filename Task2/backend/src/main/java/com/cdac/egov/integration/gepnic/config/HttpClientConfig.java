package com.cdac.egov.integration.gepnic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configures the RestTemplate for outgoing HTTP requests to GePNIC services.
 */
@Configuration
public class HttpClientConfig {

    @Bean
    public RestTemplate gepnicRestTemplate(GePNICProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) properties.getHttp().getConnectTimeout().toMillis());
        factory.setReadTimeout((int) properties.getHttp().getReadTimeout().toMillis());
        return new RestTemplate(factory);
    }
}
