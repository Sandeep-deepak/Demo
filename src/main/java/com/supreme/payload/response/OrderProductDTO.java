package com.supreme.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductDTO {
    private Long productId;
    private String productName;
    private String productImgName;
    private String productImgUrl;
    private int orderQuantity;
}
