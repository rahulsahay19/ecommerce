package com.rahulsahay.CloudGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class CloudGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudGatewayApplication.class, args);
	}

	//This will bring Circuit Breaker to spring context
	//TODO: Check for spring boot 3 implementation
//	@Bean
//	public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
//		return factory -> factory.configureDefault(
//				id -> new Resilience4JConfigBuilder(id)
//						.circuitBreakerConfig(
//								CircuitBreakerConfig.ofDefaults()
//
//						).build()
//		);
//	}
}
