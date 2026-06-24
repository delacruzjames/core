package com.example.core.controller;

import com.example.core.model.Product;
import com.example.core.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class ProductViewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProductRepository productRepository;

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();
	}

	@Test
	void listProductsRendersTemplate() throws Exception {
		productRepository.save(new Product("Laptop", "15-inch laptop", 999.99));

		mockMvc.perform(get("/products"))
				.andExpect(status().isOk())
				.andExpect(view().name("products/list"))
				.andExpect(content().string(containsString("Laptop")))
				.andExpect(content().string(containsString("999.99")));
	}

	@Test
	void showCreateFormRendersTemplate() throws Exception {
		mockMvc.perform(get("/products/new"))
				.andExpect(status().isOk())
				.andExpect(view().name("products/form"))
				.andExpect(content().string(containsString("Add Product")));
	}

	@Test
	void createProductFromForm() throws Exception {
		mockMvc.perform(post("/products")
						.param("name", "Mouse")
						.param("description", "Wireless mouse")
						.param("price", "29.99"))
				.andExpect(status().is3xxRedirection());

		assertThat(productRepository.findAll())
				.hasSize(1)
				.first()
				.extracting(Product::getName, Product::getPrice)
				.containsExactly("Mouse", 29.99);
	}

	@Test
	void viewProductDetailRendersTemplate() throws Exception {
		Product saved = productRepository.save(new Product("Keyboard", "Mechanical keyboard", 79.99));

		mockMvc.perform(get("/products/" + saved.getId()))
				.andExpect(status().isOk())
				.andExpect(view().name("products/detail"))
				.andExpect(content().string(containsString("Keyboard")));
	}

}
