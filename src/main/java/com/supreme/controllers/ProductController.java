package com.supreme.controllers;

import com.supreme.entity.Product;
import com.supreme.payload.request.ProductModel;
import com.supreme.payload.response.Response;
import com.supreme.services.ProductService;
import com.supreme.utility.ProfileUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class ProductController {

    private final ProductService productService;
    private final ProfileUtility profileUtility;

    @Autowired
    public ProductController(ProductService productService, ProfileUtility profileUtility) {
        this.productService = productService;
        this.profileUtility = profileUtility;
    }

    // Upload Product details and Image to S3
    @PostMapping(value = "/upload")
    public ResponseEntity<?> addProduct(
            @RequestParam Long categoryId,
            @ModelAttribute ProductModel productModel) {
        return productService.addProduct(categoryId, productModel);
    }

    // Fetch Product details
    @GetMapping("/details/{productId}")
    public ResponseEntity<?> getProductDetails(@PathVariable Long productId) {
        return productService.getProductDetails(productId);
    }

    // Fetch List of Products by Category Id
    @GetMapping("/list")
    public ResponseEntity<?> getProducts(@RequestParam Long categoryId) {
        return productService.getProducts(categoryId);
    }

    // Fetch List of Products
    @GetMapping("/all")
    public ResponseEntity<?> getProductsList() {
        List<Product> products = productService.getProductsList();
        products.stream().filter(product -> product.getProductImgName() != null && product.getProductImgUrl() != null)
                .forEach(product -> product.setProductImgUrl(profileUtility.generateDownloadUrl("/admin/product/download/", product.getProductName())));

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(), 1, "Products fetched successfully", "MSG23", products), HttpStatus.OK);
    }

    // Download Product Image by Product name
    @GetMapping("/download/{productName}")
    public ResponseEntity<?> getProductPicByName(@PathVariable String productName)
            throws IOException {
        return productService.getProductPicByName(productName);
    }

    // Update Product Details and image by deleting old one from S3 bucket
    @PutMapping(value = "/update/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long productId,
            @ModelAttribute ProductModel productModel // @Valid
    ) {
        return productService.updateProduct(productId, productModel);
    }

    // Delete Product
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        return productService.deleteProduct(productId);
    }

}
