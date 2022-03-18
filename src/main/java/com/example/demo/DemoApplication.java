package com.example.demo;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.reactive.ResourceHandlerRegistrationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public ResourceHandlerRegistrationCustomizer resourceCustomizer() {
		return resources -> {
			resources.setMediaTypes(Map.of("mjs", MediaType.valueOf("application/javascript")));
		};
	}
}