package com.itvillage.renttech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RentTechApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RentTechApiApplication.class, args);
	}

}
