package com.example.core.controller;

import com.example.core.model.Product;
import com.example.core.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();
	}

	@Test
	void createAndFindAll() throws Exception {
		Product product = new Product("Laptop", "15-inch laptop", 999.99);

		mockMvc.perform(post("/api/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(product)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is("Laptop")))
				.andExpect(jsonPath("$.price", is(999.99)));

		mockMvc.perform(get("/api/products"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].name", is("Laptop")));
	}

	@Test
	void findByIdAndUpdate() throws Exception {
		Product saved = productRepository.save(new Product("Phone", "Android phone", 499.99));

		mockMvc.perform(get("/api/products/" + saved.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is("Phone")));

		Product updated = new Product("Phone Pro", "Updated Android phone", 599.99);

		mockMvc.perform(put("/api/products/" + saved.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updated)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is("Phone Pro")))
				.andExpect(jsonPath("$.price", is(599.99)));
	}

	@Test
	void deleteProduct() throws Exception {
		Product saved = productRepository.save(new Product("Tablet", "10-inch tablet", 299.99));

		mockMvc.perform(delete("/api/products/" + saved.getId()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/products/" + saved.getId()))
				.andExpect(status().isNotFound());
	}

}
