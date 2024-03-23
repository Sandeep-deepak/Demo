package com.supreme.service;

import org.springframework.http.ResponseEntity;

public interface CategoryService {

    // Create Category
    ResponseEntity<?> addCategory(String categoryName);

    // Fetch Category Details
    ResponseEntity<?> getCategoryDetails(long categoryId);

    // Fetch List of categories
    ResponseEntity<?> getCategories();

    // Update Category Details
    ResponseEntity<?> updateCategory(long categoryId, String categoryName);

    // Delete Category
    ResponseEntity<?> delCategory(long categoryId);

}
