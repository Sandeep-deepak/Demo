package com.supreme.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.supreme.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ProductModel {
    private String productName;
    private MultipartFile productImage;
    @JsonIgnore
    private Category category;
}
