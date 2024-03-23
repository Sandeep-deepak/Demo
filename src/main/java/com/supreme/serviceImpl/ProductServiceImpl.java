package com.supreme.serviceImpl;

import com.supreme.entity.Category;
import com.supreme.entity.Product;
import com.supreme.payload.request.ProductModel;
import com.supreme.payload.response.ErrorResponse;
import com.supreme.payload.response.ProductResponse;
import com.supreme.payload.response.Response;
import com.supreme.repository.AppFeaturesRepo;
import com.supreme.repository.CategoryRepo;
import com.supreme.repository.ProductRepo;
import com.supreme.service.ProductService;
import com.supreme.utility.ProfileUtility;
import com.supreme.utility.S3Util;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final S3Util s3Util;
    private final ProfileUtility profileUtility;
    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private final AppFeaturesRepo appFeaturesRepo;
    private final ProductResponse productResponse;
    private final Response response;
    private final ErrorResponse errorResponse;

    @Value("${s3.product}")
    private String s3FolderName;
    @Value("${path.product}")
    private String productPath;

    @Autowired
    public ProductServiceImpl(S3Util s3Util, ProfileUtility profileUtility, CategoryRepo categoryRepo, ProductRepo productRepo, AppFeaturesRepo appFeaturesRepo, ProductResponse productResponse, Response response, ErrorResponse errorResponse) {
        this.s3Util = s3Util;
        this.profileUtility = profileUtility;
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
        this.appFeaturesRepo = appFeaturesRepo;
        this.productResponse = productResponse;
        this.response = response;
        this.errorResponse = errorResponse;
    }

    // Create Product
    @Override
    public ResponseEntity<?> addProduct(Long categoryId, ProductModel productModel) {
//        // Check if AppFeatures exists
//        Optional<AppFeatures> appFeaturesOptional = appFeaturesRepo.findById(1L);
//        if (appFeaturesOptional.isEmpty()) {
//            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "App Features doesn't exist", "MSG33"));
//        }
//        AppFeatures appFeatures = appFeaturesOptional.get();

        // Check if Category exists
        Optional<Category> categoryOptional = categoryRepo.findById(categoryId);
        if (categoryOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Category doesn't exist", "MSG33"));
        }
        Category category = categoryOptional.get();

        // Check if Product Name already exists
        if (Boolean.TRUE.equals(productRepo.existsByProductName(productModel.getProductName()))) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Product Name already exists", "MSG33"));
        }
        // Check if Product Name is provided and not empty
        if (productModel.getProductName().isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Product Name must not be null", "MSG33"));
        }
        // Check if Product image is provided
        if (productModel.getProductImage().isEmpty() || productModel.getProductImage() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Please attach the Product Image", "MSG33"));
        }

        Product product = new Product();
        product.setProductName(productModel.getProductName());
        product.setCategory(category);

        try {
            String s3Url = s3Util.uploadFile(s3FolderName, productModel.getProductImage());
            String fileName = s3Url.substring(s3Url.lastIndexOf("/") + 1);
            // Storing only URI because of frequent change in URL
            String imageDownloadUri = productPath + productModel.getProductName();

            product.setProductImgName(fileName);
            product.setProductImgUrl(imageDownloadUri);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "Failed to save image", "MSG33"));
        }

        productRepo.save(product);

        productResponse.setProductId(product.getProductId());
        productResponse.setProductName(product.getProductName());
        productResponse.setProductImgName(product.getProductImgName());
        productResponse.setProductImgUrl(profileUtility.generateDownloadUrl(productPath, product.getProductName()));

        return ResponseEntity.ok().body(new Response(HttpStatus.OK.value(), 1, "Product added successfully", "MSG17", productResponse));
    }

    // Fetch Product details
    @Override
    public ResponseEntity<?> getProductDetails(Long productId) {
        Optional<Product> productOptional = productRepo.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (product.getProductImgName() != null && product.getProductImgUrl() != null) {
                productResponse.setProductId(product.getProductId());
                productResponse.setProductName(product.getProductName());
                productResponse.setProductImgName(product.getProductImgName());
                productResponse.setProductImgUrl(profileUtility.generateDownloadUrl(productPath, product.getProductName()));

                response.setStatusCode(HttpStatus.OK.value());
                response.setStatus(1);
                response.setMessageCode("MSG17");
                response.setMessage("Product Details fetched successfully");
                response.setResult(productResponse);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 0, "Product Picture Not Found", "MSG27"));
            }
        } else {
            errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            errorResponse.setStatus(0);
            errorResponse.setMessageCode("MSG48");
            errorResponse.setMessage("Product Doesn't exists");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    // Fetch List of Products by Category id
    @Override
    public ResponseEntity<?> getProducts(Long categoryId) {
//        // Check if AppFeatures exists
//        Optional<AppFeatures> appFeaturesOptional = appFeaturesRepo.findById(1L);
//        if (appFeaturesOptional.isEmpty()) {
//            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "App Features doesn't exist", "MSG33"));
//        }
//        AppFeatures appFeatures = appFeaturesOptional.get();

        // Check if Category exists
        Optional<Category> categoryOptional = categoryRepo.findById(categoryId);
        if (categoryOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Category doesn't exist", "MSG33"));
        }
        Category category = categoryOptional.get();

        List<Product> products;
        products = category.getProducts();

        List<Product> updatedProductList = products.stream()
                .map(productObject -> {
                    Product updatedProduct = new Product();
                    updatedProduct.setProductId(productObject.getProductId());
                    updatedProduct.setProductName(productObject.getProductName());
                    updatedProduct.setProductImgName(productObject.getProductImgName());
                    updatedProduct.setProductImgUrl(profileUtility.generateDownloadUrl(productPath, productObject.getProductName()));
                    return updatedProduct;
                }).toList();

        response.setStatusCode(HttpStatus.OK.value());
        response.setStatus(1);
        response.setMessageCode("MSG16");
        response.setMessage("Products list fetched successfully");
        response.setResult(updatedProductList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Fetch List of Products
    @Override
    public List<Product> getProductsList() {
        return productRepo.findAll();
    }

    // Update Product Details and image(deletes old one from S3 bucket)
    @Override
    public ResponseEntity<?> updateProduct(Long productId, ProductModel productModel) { // Long categoryId, Long subCategoryId,
        // Check if Product exists
        Optional<Product> productOptional = productRepo.findById(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Product doesn't exist", "MSG33"));
        }
        Product product = productOptional.get();

        if (productModel.getProductName() != null && !productModel.getProductName().isBlank()) {
            // Check if Product Name already exists
            if (Boolean.TRUE.equals(productRepo.existsByProductName(productModel.getProductName()))) {
                return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Product Name already exists", "MSG33"));
            }
            product.setProductName(productModel.getProductName());
        }

        if (productModel.getProductImage() != null && !productModel.getProductImage().isEmpty()) {
            if (product.getProductImgUrl() != null && product.getProductImgName() != null) {
                s3Util.deleteFileFromS3Bucket(s3FolderName, product.getProductImgName());
                product.setProductImgName(null);
                product.setProductImgUrl(null);
            }
            String s3Url = s3Util.uploadFile(s3FolderName, productModel.getProductImage());
            String fileName = s3Url.substring(s3Url.lastIndexOf("/") + 1);
            String imageDownloadUri = productPath + product.getProductImgName();

            product.setProductImgName(fileName);
            product.setProductImgUrl(imageDownloadUri);
        }

        productRepo.save(product);

        productResponse.setProductId(product.getProductId());
        productResponse.setProductName(product.getProductName());
        productResponse.setProductImgName(product.getProductImgName());
        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path(productPath)
                .path(product.getProductName()).toUriString();
        productResponse.setProductImgUrl(downloadUrl);

        response.setStatusCode(HttpStatus.OK.value());
        response.setStatus(1);
        response.setMessageCode("MSG13");
        response.setMessage("Product updated successfully");
        response.setResult(productResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Delete Product by id
    @Override
    public ResponseEntity<?> deleteProduct(Long productId) {
        Optional<Product> productOptional = productRepo.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (product.getProductImgUrl() != null && product.getProductImgName() != null) {
                s3Util.deleteFileFromS3Bucket(s3FolderName, product.getProductImgName());
                product.setProductImgName(null);
                product.setProductImgUrl(null);
            } else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ErrorResponse(HttpStatus.OK.value(), 0, "Image Not Found", "MSG26"));
            }
            productRepo.deleteById(productId);

            response.setStatusCode(HttpStatus.NO_CONTENT.value());
            response.setStatus(1);
            response.setMessageCode("MSG13");
            response.setMessage("Product deleted successfully");
            response.setResult(null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            errorResponse.setStatus(0);
            errorResponse.setMessageCode("MSG48");
            errorResponse.setMessage("Product doesn't exists");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    // Download Product Image by Product name
    @Override
    public ResponseEntity<?> getProductPicByName(String productName) throws IOException {
        Optional<Product> productOptional = productRepo.findByProductName(productName);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (product.getProductImgName() != null && product.getProductImgUrl() != null) {
                return s3Util.getImageFromS3Bucket(s3FolderName, product.getProductImgName());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 0, "Image Not Found", "MSG26"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 0, "Product Doesn't exists", "MSG11"));
        }
    }

}
