package com.example.core.client;

import com.example.core.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductClient {

	private final RestTemplate restTemplate;
	private final String baseUrl;

	public ProductClient(
			RestTemplate restTemplate,
			@Value("${api.base-url:http://localhost:8080}") String baseUrl) {
		this.restTemplate = restTemplate;
		this.baseUrl = baseUrl;
	}

	public List<Product> getAllProducts() {
		Product[] products = restTemplate.getForObject(baseUrl + "/api/products", Product[].class);
		return products == null ? List.of() : Arrays.asList(products);
	}

	public Product getProduct(Long id) {
		return restTemplate.getForObject(baseUrl + "/api/products/" + id, Product.class);
	}

	public Product createProduct(Product product) {
		return restTemplate.postForObject(baseUrl + "/api/products", product, Product.class);
	}

	public Product updateProduct(Long id, Product product) {
		restTemplate.put(baseUrl + "/api/products/" + id, product);
		return getProduct(id);
	}

}
