package com.financeapp.personalfinance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class PersonalFinanceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonalFinanceApiApplication.class, args);
		System.out.println("🚀 Personal Finance API is running!");
		System.out.println("📖 API Documentation: http://localhost:8080/");
		System.out.println("🔍 Health Check: http://localhost:8080/actuator/health");

	}

}
