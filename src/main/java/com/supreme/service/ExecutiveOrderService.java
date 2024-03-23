package com.supreme.service;

import com.supreme.payload.request.OrderModel;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface ExecutiveOrderService {

    // Create Executive Order
    ResponseEntity<?> addOrder(OrderModel orderRequest);

    // Fetch Executive order by order id
    ResponseEntity<?> getOrder(Long orderId);

    // Fetch List of Executive orders
    ResponseEntity<?> getExecutiveOrders(Long executiveId);

    // Fetch Executive order image
    ResponseEntity<?> getOrderPic(String orderImgName) throws IOException;

}
