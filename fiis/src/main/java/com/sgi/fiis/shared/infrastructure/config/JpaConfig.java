package com.sgi.fiis.shared.infrastructure.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackages = {"com.sgi.fiis"})
@EntityScan(basePackages = {"com.sgi.fiis"})
public class JpaConfig {
}
