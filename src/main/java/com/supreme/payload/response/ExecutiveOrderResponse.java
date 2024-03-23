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
public class ExecutiveOrderResponse {

    private Long executiveId;
    private String executiveName;
    private ERole role;
    private Long outletId;
    private String outletName;

    private Long orderId;
    private LocalDateTime dateOfOrder;
    private String orderImgName;
    private String orderImgUrl;
    private String orderLongitude;
    private String orderLatitude;
    private List<OrderProductDTO> products;
}
