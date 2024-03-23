package com.supreme.serviceImpl;

import com.supreme.entity.Category;
import com.supreme.payload.response.CategoryResponse;
import com.supreme.payload.response.ErrorResponse;
import com.supreme.payload.response.Response;
import com.supreme.repository.AppFeaturesRepo;
import com.supreme.repository.CategoryRepo;
import com.supreme.service.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryResponse categoryResponse;
    private final AppFeaturesRepo appFeaturesRepo;
    private final CategoryRepo categoryRepo;
    private final Response response;
    private final ErrorResponse errorResponse;

    @Autowired
    public CategoryServiceImpl(CategoryResponse categoryResponse, AppFeaturesRepo appFeaturesRepo, CategoryRepo categoryRepo, Response response, ErrorResponse errorResponse) {
        this.categoryResponse = categoryResponse;
        this.appFeaturesRepo = appFeaturesRepo;
        this.categoryRepo = categoryRepo;
        this.response = response;
        this.errorResponse = errorResponse;
    }

    // Create Category
    @Override
    public ResponseEntity<?> addCategory(String categoryName) {
        if (Boolean.TRUE.equals(categoryRepo.existsByCategoryName(categoryName))) {
            errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            errorResponse.setStatus(0);
            errorResponse.setMessageCode("MSG33");
            errorResponse.setMessage("Category Name already exists");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Category category = new Category();
        if (!categoryName.isBlank()) {
            category.setCategoryName(categoryName);
        } else {
            errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            errorResponse.setStatus(0);
            errorResponse.setMessageCode("MSG34");
            errorResponse.setMessage("Category Name must not be null");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        categoryRepo.save(category);

        categoryResponse.setCategoryId(category.getCategoryId());
        categoryResponse.setCategoryName(category.getCategoryName());

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response((HttpStatus.CREATED.value()), 1, "Category added successfully", "MSG28", categoryResponse));
    }

    // Fetch Category Details
    @Override
    public ResponseEntity<?> getCategoryDetails(long categoryId) {
        Optional<Category> categoryOpt = categoryRepo.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            errorResponse.setStatus(0);
            errorResponse.setMessageCode("MSG48");
            errorResponse.setMessage("Category Doesn't exists");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        Category category = categoryOpt.get();
        categoryResponse.setCategoryId(category.getCategoryId());
        categoryResponse.setCategoryName(category.getCategoryName());

        response.setStatusCode(HttpStatus.OK.value());
        response.setStatus(1);
        response.setMessageCode("MSG17");
        response.setMessage("Category Details fetched successfully");
        response.setResult(categoryResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Fetch List of categories
    @Override
    public ResponseEntity<?> getCategories() {
        List<Category> categoriesList = categoryRepo.findAll();
        List<CategoryResponse> updatedCategoryList = categoriesList.stream()
                .map(category -> {
                    CategoryResponse updatedCategory = new CategoryResponse();
                    updatedCategory.setCategoryId(category.getCategoryId());
                    updatedCategory.setCategoryName(category.getCategoryName());
                    return updatedCategory;
                }).toList(); //.collect(Collectors.toList());

        response.setStatusCode(HttpStatus.OK.value());
        response.setStatus(1);
        response.setMessageCode("MSG16");
        response.setMessage("Categories list fetched successfully");
        response.setResult(updatedCategoryList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Update Category Details
    @Override
    public ResponseEntity<?> updateCategory(long categoryId, String categoryName) {
        Optional<Category> categoryOpt = categoryRepo.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            errorResponse.setStatus(0);
            errorResponse.setMessageCode("MSG48");
            errorResponse.setMessage("Category Doesn't exists");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        Category category = categoryOpt.get();
        if (categoryName != null && !categoryName.isBlank()) {
            if (Boolean.TRUE.equals(categoryRepo.existsByCategoryName(categoryName))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Category Name already exists", "MSG33"));
            }
            category.setCategoryName(categoryName);
            categoryRepo.save(category);
        }

        categoryResponse.setCategoryId(category.getCategoryId());
        categoryResponse.setCategoryName(category.getCategoryName());

        response.setStatusCode(HttpStatus.OK.value());
        response.setStatus(1);
        response.setMessageCode("MSG13");
        response.setMessage("Category updated successfully");
        response.setResult(categoryResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Delete Category
    @Override
    public ResponseEntity<?> delCategory(long categoryId) {
        // Check if Category exists
        Optional<Category> categoryOpt = categoryRepo.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Category doesn't exist", "MSG33"));
        }

        categoryRepo.deleteById(categoryId);

        response.setStatusCode(HttpStatus.NO_CONTENT.value());
        response.setStatus(1);
        response.setMessageCode("MSG13");
        response.setMessage("Category deleted successfully");
        response.setResult(null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
