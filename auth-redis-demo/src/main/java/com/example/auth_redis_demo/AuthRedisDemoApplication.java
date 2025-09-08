package com.example.auth_redis_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AuthRedisDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthRedisDemoApplication.class, args);
	}
}

