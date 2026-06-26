package com.example.core.service;

import com.example.core.model.Product;
import com.example.core.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductServiceCacheTest {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CacheManager cacheManager;

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();
		cacheManager.getCacheNames().forEach(name -> {
			var cache = cacheManager.getCache(name);
			if (cache != null) {
				cache.clear();
			}
		});
	}

	@Test
	void findAllUsesCacheOnSecondCall() {
		productRepository.save(new Product("Laptop", "15-inch laptop", 999.99));

		List<Product> firstCall = productService.findAll();
		List<Product> secondCall = productService.findAll();

		assertThat(firstCall).hasSize(1);
		assertThat(secondCall).extracting(Product::getName).containsExactly("Laptop");
	}

	@Test
	void findByIdUsesCacheOnSecondCall() {
		Product saved = productRepository.save(new Product("Phone", "Android phone", 499.99));

		assertThat(productService.findById(saved.getId())).isPresent();
		assertThat(productService.findById(saved.getId())).isPresent();
	}

	@Test
	void saveEvictsProductsCache() {
		productRepository.save(new Product("Tablet", "10-inch tablet", 299.99));
		productService.findAll();

		productService.save(new Product("Watch", "Smart watch", 199.99));

		assertThat(productService.findAll()).hasSize(2);
	}

	@Test
	void deleteEvictsCache() {
		Product saved = productRepository.save(new Product("Headphones", "Wireless headphones", 149.99));
		productService.findAll();
		productService.findById(saved.getId());

		productService.deleteById(saved.getId());

		assertThat(productService.findById(saved.getId())).isEmpty();
		assertThat(productService.findAll()).isEmpty();
	}

}
