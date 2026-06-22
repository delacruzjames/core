package com.example.core.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class DevProfileTest {

	@Value("${api.base-url}")
	private String apiBaseUrl;

	@Autowired
	private Environment environment;

	@Test
	void loadsDevRestUrl() {
		assertThat(apiBaseUrl).isEqualTo("http://localhost:8080");
		assertThat(environment.getActiveProfiles()).containsExactly("dev");
	}

}
