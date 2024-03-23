package com.supreme.payload.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignProductRequest {
    private Long distributorId;
    private Long productId;
    private int quantity;
}
