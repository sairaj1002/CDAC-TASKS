package com.cdac.egov.integration.gepnic.repository;

import com.cdac.egov.integration.gepnic.entity.IntegrationConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IntegrationConfigurationRepository extends JpaRepository<IntegrationConfiguration, Long> {

    Optional<IntegrationConfiguration> findByEnvironmentNameAndActiveTrue(String environmentName);

    Optional<IntegrationConfiguration> findFirstByActiveTrueOrderByUpdatedAtDesc();
}
