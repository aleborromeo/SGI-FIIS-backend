package com.sgi.fiis.thesis.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.sgi.fiis")
public class ThesisJpaAutoConfiguration {
}
