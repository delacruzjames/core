package com.example.core.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CoreInfoContributor implements InfoContributor {

	private final Environment environment;

	public CoreInfoContributor(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void contribute(Info.Builder builder) {
		builder.withDetail("activeProfiles", Arrays.asList(environment.getActiveProfiles()))
				.withDetail("api", "/api/products")
				.withDetail("documentation", "Spring Boot Health Checks and Metrics training");
	}

}
