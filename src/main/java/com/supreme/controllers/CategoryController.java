package com.supreme.controllers;

import com.supreme.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Upload Category Image to S3
    @PostMapping(value = "/upload") //, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addCategory(
            @RequestPart(name = "name") @Valid String categoryName) {
        return categoryService.addCategory(categoryName);
    }

    // Fetch Category Details
    @GetMapping("/details/{categoryId}")
    public ResponseEntity<?> getCategoryDetails(@PathVariable long categoryId) {
        return categoryService.getCategoryDetails(categoryId);
    }

    // Fetch List of categories
    @GetMapping("/getCategories")
    public ResponseEntity<?> getCategories() {
        return categoryService.getCategories();
    }

    // Update Category Details
    @PutMapping(value = "/update/{categoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCategory(@PathVariable long categoryId, @RequestParam(value = "name") String categoryName) {
        return categoryService.updateCategory(categoryId, categoryName);
    }

    // Delete Category
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<?> delCategory(@PathVariable long categoryId) {
        return categoryService.delCategory(categoryId);
    }
}
