package com.example.core.client;

import com.example.core.model.Product;
import com.example.core.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductClientTest {

	@LocalServerPort
	private int port;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ProductRepository productRepository;

	private ProductClient productClient;

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();
		productClient = new ProductClient(restTemplate, "http://localhost:" + port);
	}

	@Test
	void getAllProducts() {
		productRepository.save(new Product("Mouse", "Wireless mouse", 29.99));

		assertThat(productClient.getAllProducts())
				.hasSize(1)
				.first()
				.extracting(Product::getName, Product::getPrice)
				.containsExactly("Mouse", 29.99);
	}

	@Test
	void getProduct() {
		Product saved = productRepository.save(new Product("Keyboard", "Mechanical keyboard", 79.99));

		Product found = productClient.getProduct(saved.getId());

		assertThat(found.getName()).isEqualTo("Keyboard");
		assertThat(found.getPrice()).isEqualTo(79.99);
	}

	@Test
	void createProduct() {
		Product created = productClient.createProduct(new Product("Monitor", "27-inch monitor", 249.99));

		assertThat(created.getId()).isNotNull();
		assertThat(productRepository.findAll()).hasSize(1);
	}

	@Test
	void updateProduct() {
		Product saved = productRepository.save(new Product("Headphones", "Noise cancelling", 199.99));

		Product updated = productClient.updateProduct(
				saved.getId(),
				new Product("Headphones Pro", "Updated model", 249.99));

		assertThat(updated.getName()).isEqualTo("Headphones Pro");
		assertThat(updated.getPrice()).isEqualTo(249.99);
	}

}
