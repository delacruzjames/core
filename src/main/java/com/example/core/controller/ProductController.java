package com.example.core.controller;

import com.example.core.model.Product;
import com.example.core.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public List<Product> getAllProducts() {
		LOGGER.info("Fetching all products");
		List<Product> products = productService.findAll();
		LOGGER.debug("Found {} products", products.size());
		return products;
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> getProduct(@PathVariable Long id) {
		LOGGER.info("Fetching product with id: {}", id);
		return productService.findById(id)
				.map(product -> {
					LOGGER.debug("Product found: {}", product.getName());
					return ResponseEntity.ok(product);
				})
				.orElseGet(() -> {
					LOGGER.warn("Product not found with id: {}", id);
					return ResponseEntity.notFound().build();
				});
	}

	@PostMapping
	public Product createProduct(@RequestBody Product product) {
		LOGGER.info("Creating product: {}", product.getName());
		Product saved = productService.save(product);
		LOGGER.debug("Created product with id: {}", saved.getId());
		return saved;
	}

	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
		LOGGER.info("Updating product with id: {}", id);
		return productService.findById(id)
				.map(product -> {
					product.setName(productDetails.getName());
					product.setDescription(productDetails.getDescription());
					product.setPrice(productDetails.getPrice());
					Product updated = productService.update(product);
					LOGGER.debug("Updated product: {}", updated.getName());
					return ResponseEntity.ok(updated);
				})
				.orElseGet(() -> {
					LOGGER.warn("Product not found for update with id: {}", id);
					return ResponseEntity.notFound().build();
				});
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
		LOGGER.info("Deleting product with id: {}", id);
		if (!productService.existsById(id)) {
			LOGGER.warn("Product not found for delete with id: {}", id);
			return ResponseEntity.notFound().build();
		}
		productService.deleteById(id);
		LOGGER.debug("Deleted product with id: {}", id);
		return ResponseEntity.noContent().build();
	}

}
