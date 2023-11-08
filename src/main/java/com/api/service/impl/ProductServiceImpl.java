package com.api.service.impl;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.api.entity.Product;
import com.api.exception.ResourceNotFoundException;
import com.api.repository.ProductRepository;
import com.api.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
		super();
		this.productRepository = productRepository;
	}

	@Override
    public Product createProduct(Product product, MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String encodeToString = Base64.getEncoder().encodeToString(bytes);
        product.setImages(encodeToString);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(long productId, Product product, MultipartFile file) throws IOException {
        Product existingProduct = productRepository.findById(productId)
        		.orElseThrow(() -> new ResourceNotFoundException("Product Id not found: " + productId));


        byte[] bytes = file.getBytes();
        String encodeToString = Base64.getEncoder().encodeToString(bytes);
        existingProduct.setImages(encodeToString);
        existingProduct.setCreated(product.getCreated());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setProductBrand(product.getProductBrand());
        existingProduct.setProductName(product.getProductName());
        existingProduct.setProductQuantity(product.getProductQuantity());

        return productRepository.save(existingProduct);
    }

    @Override
    public boolean deleteProduct(long productId) {
        Optional<Product> optional = productRepository.findById(productId);
        optional.orElseThrow(() -> new ResourceNotFoundException("Product Id not found: " + productId));
        if (optional.isPresent()) {
            productRepository.delete(optional.get());
            return true;
        }
        return false;
    }

    @Override
    public Product getProductById(long productId) {
        Product product = productRepository.findById(productId)
        		.orElseThrow(() -> new ResourceNotFoundException("Product Id not found: " + productId));
        return product;
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> list = productRepository.findAll();
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("Product DB is Empty");
        }return list;
    }
    @Override
    public List<Product> getProductsByPagination(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Product> page = productRepository.findAll(pageable);
        return page.getContent();
    }
    }