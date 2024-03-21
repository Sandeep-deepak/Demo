package com.supreme.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.supreme.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ProductResponse {
    private Long productId;
    private String productName;
    private String productImgName;
    private String productImgUrl;
    @JsonIgnore
    private Category category;
}
