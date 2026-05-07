package com.bellamyphan.finora_2026_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class Finora2026SpringApplication {

	static void main(String[] args) {
		SpringApplication.run(Finora2026SpringApplication.class, args);
	}

}
