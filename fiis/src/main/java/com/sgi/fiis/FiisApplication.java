package com.sgi.fiis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FiisApplication {

	static {
		try {
			Class.forName("com.sgi.fiis.shared.infrastructure.config.EnvLoaderConfig");
		} catch (ClassNotFoundException e) {
			// ignore
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(FiisApplication.class, args);
	}

}
