package com.cdac.egov.integration.gepnic.pretender.config;

import com.cdac.egov.integration.gepnic.pretender.works.MockWorksService;
import com.cdac.egov.integration.gepnic.pretender.works.WorksService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class WorksProviderConfiguration {
    @Bean
    @Profile("!prod")
    @ConditionalOnProperty(prefix = "pretender.works", name = "provider", havingValue = "mock")
    public WorksService mockWorksService() {
        return new MockWorksService();
    }

    @Bean
    @Profile("prod")
    @ConditionalOnMissingBean(WorksService.class)
    public WorksService requireProductionWorksAdapter() {
        throw new IllegalStateException("Production startup requires an approved WAMIS Core WorksService adapter. The development mock is disabled in the prod profile.");
    }
}
