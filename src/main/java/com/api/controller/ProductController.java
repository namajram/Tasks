package com.api.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.entity.Product;
import com.api.service.impl.ProductServiceImpl;

@RestController
@RequestMapping("/api/Product")
public class ProductController {

	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	private ProductServiceImpl productServiceImpl;

	@PostMapping()
	public ResponseEntity<?> createProduct(@ModelAttribute Product product, @RequestParam("image") MultipartFile file)
			throws Throwable {
		try {
			Product products = productServiceImpl.createProduct(product, file);
			return ResponseEntity.ok(products);

		} catch (Exception e) {
			logger.error("Error in POST /api/Product", e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	@PutMapping("/{id}")

	public ResponseEntity<?> updateProduct(@PathVariable Long id, @ModelAttribute Product product,
			@RequestParam("image") MultipartFile file) throws Throwable {

		try {
			Product products = productServiceImpl.updateProduct(id, product, file);
			if (products != null) {
				return ResponseEntity.ok(products);
			}
			return ResponseEntity.notFound().build();

		} catch (Exception e) {
			logger.error("Error in PUT /api/Product/{id}:" + id, e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")

	public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
		try {
			boolean deleted = productServiceImpl.deleteProduct(id);
			if (deleted) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.notFound().build();

		} catch (Exception e) {
			logger.error("Error in DELETE /api/Product/{id}:" + id, e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	@GetMapping("/{id}")

	public ResponseEntity<?> getProductById(@PathVariable Long id) {
		try {
			Product product = productServiceImpl.getProductById(id);
			if (product != null) {
				return ResponseEntity.ok(product);
			}
			return ResponseEntity.notFound().build();

		} catch (Exception e) {
			logger.error("Error in GET /api/Product/{id}:" + id, e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	@GetMapping("/all")

	public ResponseEntity<?> getAllProduct() {
		try {
			List<Product> product = productServiceImpl.getAllProducts();
			if (product.isEmpty()) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.ok(product);

		} catch (Exception e) {
			logger.error("Error in GET /api/Product/all", e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}
}
