package com.royalsmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RoyalsmarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoyalsmarketApplication.class, args);
	}

}
