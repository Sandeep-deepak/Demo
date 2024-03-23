package com.supreme.service;

import com.supreme.payload.request.OrderModel;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface DistributorOrderService {

    // Create Distributor Order
    ResponseEntity<?> addOrder(OrderModel orderRequest);

    // Fetch Distributor order by order id
    ResponseEntity<?> getOrder(Long orderId);

    // Fetch List of Distributor orders
    ResponseEntity<?> getDistributorOrders(Long distributorId);

    // Fetch Distributor order image
    ResponseEntity<?> getOrderPic(String orderImgName) throws IOException;

}
