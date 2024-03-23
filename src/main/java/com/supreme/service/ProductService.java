package com.supreme.service;

import com.supreme.entity.Product;
import com.supreme.payload.request.ProductModel;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    // Create Product
    ResponseEntity<?> addProduct(Long categoryId, ProductModel productModel);

    // Fetch Product details
    ResponseEntity<?> getProductDetails(Long productId);

    // Fetch List of Products by Category id
    ResponseEntity<?> getProducts(Long categoryId);

    // Fetch List of Products
    List<Product> getProductsList();

    // Update Product Details and image(deletes old one from S3 bucket)
    ResponseEntity<?> updateProduct(Long productId, ProductModel productModel);

    // Delete Product by id
    ResponseEntity<?> deleteProduct(Long productId);

    // Download Product Image by Product name
    ResponseEntity<?> getProductPicByName(String productName) throws IOException;

}
