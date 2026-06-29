package com.sgi.fiis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FiisApplication {

	public static void main(String[] args) {
		try {
			Class.forName("com.sgi.fiis.shared.infrastructure.config.EnvLoaderConfig");
		} catch (ClassNotFoundException e) {
			// ignore
		}
		SpringApplication.run(FiisApplication.class, args);
	}

}
