package com.supreme.controllers;

import com.supreme.serviceImpl.CategoryServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    private final CategoryServiceImpl categoryServiceImpl;

    @Autowired
    public CategoryController(CategoryServiceImpl categoryServiceImpl) {
        this.categoryServiceImpl = categoryServiceImpl;
    }

    // Create Category
    @PostMapping(value = "/upload") //, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addCategory(
            @RequestPart(name = "name") @Valid String categoryName) {
        return categoryServiceImpl.addCategory(categoryName);
    }

    // Fetch Category Details
    @GetMapping("/details/{categoryId}")
    public ResponseEntity<?> getCategoryDetails(@PathVariable long categoryId) {
        return categoryServiceImpl.getCategoryDetails(categoryId);
    }

    // Fetch List of categories
    @GetMapping("/getCategories")
    public ResponseEntity<?> getCategories() {
        return categoryServiceImpl.getCategories();
    }

    // Update Category Details
    @PutMapping(value = "/update/{categoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCategory(@PathVariable long categoryId, @RequestParam(value = "name") String categoryName) {
        return categoryServiceImpl.updateCategory(categoryId, categoryName);
    }

    // Delete Category
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<?> delCategory(@PathVariable long categoryId) {
        return categoryServiceImpl.delCategory(categoryId);
    }
    
}
