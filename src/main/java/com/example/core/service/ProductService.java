package com.example.core.service;

import com.example.core.model.Product;
import com.example.core.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Cacheable(value = "products")
	public List<Product> findAll() {
		LOGGER.info("Loading all products from database");
		return productRepository.findAll();
	}

	@Cacheable(value = "product", key = "#id")
	public Optional<Product> findById(Long id) {
		LOGGER.info("Loading product {} from database", id);
		return productRepository.findById(id);
	}

	public boolean existsById(Long id) {
		return productRepository.existsById(id);
	}

	@CachePut(value = "product", key = "#result.id")
	@CacheEvict(value = "products", allEntries = true)
	public Product save(Product product) {
		LOGGER.info("Saving product to database: {}", product.getName());
		return productRepository.save(product);
	}

	@CachePut(value = "product", key = "#product.id")
	@CacheEvict(value = "products", allEntries = true)
	public Product update(Product product) {
		LOGGER.info("Updating product in database: {}", product.getName());
		return productRepository.save(product);
	}

	@Caching(evict = {
			@CacheEvict(value = "product", key = "#id"),
			@CacheEvict(value = "products", allEntries = true)
	})
	public void deleteById(Long id) {
		LOGGER.info("Deleting product {} from database", id);
		productRepository.deleteById(id);
	}

}
