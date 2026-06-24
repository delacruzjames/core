package com.example.core.health;

import com.example.core.repository.ProductRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ProductHealthIndicator implements HealthIndicator {

	private final ProductRepository productRepository;

	public ProductHealthIndicator(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public Health health() {
		try {
			long productCount = productRepository.count();
			return Health.up()
					.withDetail("products", productCount)
					.withDetail("message", "Product database is reachable")
					.build();
		}
		catch (Exception ex) {
			return Health.down(ex)
					.withDetail("message", "Product database is not reachable")
					.build();
		}
	}

}
