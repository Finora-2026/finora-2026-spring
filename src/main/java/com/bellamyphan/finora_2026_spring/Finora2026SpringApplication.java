package com.bellamyphan.finora_2026_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(
		basePackages = "com.bellamyphan.finora_2026_spring.postgres.repository"
)
@EntityScan(
		basePackages = "com.bellamyphan.finora_2026_spring.postgres.entity"
)
@EnableMongoRepositories(
		basePackages = "com.bellamyphan.finora_2026_spring.mongodb.repository"
)
@EnableScheduling
@EnableAsync
public class Finora2026SpringApplication {

	static void main(String[] args) {
		SpringApplication.run(Finora2026SpringApplication.class, args);
	}

}
