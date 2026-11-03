package in.gov.wamis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * WAMIS - Works and Accounts Management Information System
 * Main application entry point for the WAMIS Backend and GePNIC Integration.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class WamisApplication {

    public static void main(String[] args) {
        SpringApplication.run(WamisApplication.class, args);
    }
}

