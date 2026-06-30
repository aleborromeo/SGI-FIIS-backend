package com.sgi.fiis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.sgi.fiis", "pe.unas.fiis.sgifiis.thesis"})
@EnableJpaRepositories(basePackages = {"com.sgi.fiis", "pe.unas.fiis.sgifiis.thesis"})
@EntityScan(basePackages = {"com.sgi.fiis", "pe.unas.fiis.sgifiis.thesis"})
public class FiisApplication {

	public static void main(String[] args) {
		SpringApplication.run(FiisApplication.class, args);
	}

}
