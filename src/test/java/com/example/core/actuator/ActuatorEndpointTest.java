package com.example.core.actuator;

import com.example.core.model.Product;
import com.example.core.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ActuatorEndpointTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProductRepository productRepository;

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();
	}

	@Test
	void healthEndpointIsUpWithDetails() throws Exception {
		productRepository.save(new Product("Mouse", "Wireless mouse", 29.99));

		mockMvc.perform(get("/actuator/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is("UP")))
				.andExpect(jsonPath("$.components.product.status", is("UP")))
				.andExpect(jsonPath("$.components.product.details.products", is(1)));
	}

	@Test
	void infoEndpointIncludesCustomAndBuildDetails() throws Exception {
		mockMvc.perform(get("/actuator/info"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.app.name", is("Product API")))
				.andExpect(jsonPath("$.app.team", is("Java Training")))
				.andExpect(jsonPath("$.activeProfiles", hasItem("dev")))
				.andExpect(jsonPath("$.api", is("/api/products")))
				.andExpect(jsonPath("$.build.version", notNullValue()));
	}

	@Test
	void metricsEndpointIsExposed() throws Exception {
		mockMvc.perform(get("/actuator/metrics"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.names", hasItem("jvm.memory.used")));
	}

}
