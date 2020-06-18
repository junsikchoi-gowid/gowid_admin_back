package com.nomadconnection.dapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class SecuKeypadApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecuKeypadApplication.class, args);
	}

	@PostConstruct
	private void initialize() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
}
