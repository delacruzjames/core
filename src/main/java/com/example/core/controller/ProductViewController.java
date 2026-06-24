package com.example.core.controller;

import com.example.core.model.Product;
import com.example.core.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
public class ProductViewController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductViewController.class);

	private final ProductRepository productRepository;

	public ProductViewController(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@GetMapping
	public String listProducts(Model model) {
		model.addAttribute("products", productRepository.findAll());
		model.addAttribute("pageTitle", "Product Catalog");
		LOGGER.info("Rendering product list template");
		return "products/list";
	}

	@GetMapping("/{id}")
	public String viewProduct(@PathVariable Long id, Model model) {
		return productRepository.findById(id)
				.map(product -> {
					model.addAttribute("product", product);
					model.addAttribute("pageTitle", product.getName());
					return "products/detail";
				})
				.orElse("redirect:/products");
	}

	@GetMapping("/new")
	public String showCreateForm(Model model) {
		model.addAttribute("product", new Product());
		model.addAttribute("pageTitle", "Add Product");
		return "products/form";
	}

	@PostMapping
	public String createProduct(@ModelAttribute Product product) {
		productRepository.save(product);
		LOGGER.info("Created product from form: {}", product.getName());
		return "redirect:/products";
	}

	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Long id, Model model) {
		return productRepository.findById(id)
				.map(product -> {
					model.addAttribute("product", product);
					model.addAttribute("pageTitle", "Edit Product");
					return "products/form";
				})
				.orElse("redirect:/products");
	}

	@PostMapping("/{id}")
	public String updateProduct(@PathVariable Long id, @ModelAttribute Product product) {
		return productRepository.findById(id)
				.map(existing -> {
					existing.setName(product.getName());
					existing.setDescription(product.getDescription());
					existing.setPrice(product.getPrice());
					productRepository.save(existing);
					LOGGER.info("Updated product from form: {}", existing.getName());
					return "redirect:/products";
				})
				.orElse("redirect:/products");
	}

	@PostMapping("/{id}/delete")
	public String deleteProduct(@PathVariable Long id) {
		if (productRepository.existsById(id)) {
			productRepository.deleteById(id);
			LOGGER.info("Deleted product from form with id: {}", id);
		}
		return "redirect:/products";
	}

}
