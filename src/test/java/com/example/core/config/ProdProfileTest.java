package com.example.core.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("prod")
class ProdProfileTest {

	@Value("${api.base-url}")
	private String apiBaseUrl;

	@Value("${server.port}")
	private String serverPort;

	@Autowired
	private Environment environment;

	@Test
	void loadsProdRestUrl() {
		assertThat(apiBaseUrl).isEqualTo("http://localhost:9090");
		assertThat(serverPort).isEqualTo("9090");
		assertThat(environment.getActiveProfiles()).containsExactly("prod");
	}

}
