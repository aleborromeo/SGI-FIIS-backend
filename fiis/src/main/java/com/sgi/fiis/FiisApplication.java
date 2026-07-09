package com.sgi.fiis;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = {"com.sgi.fiis", "pe.unas.fiis.sgifiis.thesis"})
@EnableJpaRepositories(basePackages = {"com.sgi.fiis", "pe.unas.fiis.sgifiis.thesis"})
@EntityScan(basePackages = {"com.sgi.fiis", "pe.unas.fiis.sgifiis.thesis"})
public class FiisApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(FiisApplication.class, args);
    }

}
