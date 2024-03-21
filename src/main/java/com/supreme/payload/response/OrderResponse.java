package com.supreme.payload.response;

import com.supreme.entity.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class OrderResponse {
    private Long orderId;
    private Long distributorId;
    private Long executiveId;
    private Long outletId;
    private LocalDateTime dateOfOrder;
    private List<OrderProductDTO> products;
    private String orderImgName;
    private String orderImgUrl;
    private String orderLongitude;
    private String orderLatitude;

    private ERole role;
    private String fullName;
}
