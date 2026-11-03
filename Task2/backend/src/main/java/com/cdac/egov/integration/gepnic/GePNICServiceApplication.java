package com.cdac.egov.integration.gepnic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * GePNIC Spring Boot Integration Service & WAMIS Backend.
 * Acts as a secure, separately deployable bridge between WAMIS and NIC-GePNIC.
 */
@SpringBootApplication(scanBasePackages = {"com.cdac.egov"})
@ConfigurationPropertiesScan(basePackages = {"com.cdac.egov"})
@EnableScheduling
public class GePNICServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GePNICServiceApplication.class, args);
    }
}
