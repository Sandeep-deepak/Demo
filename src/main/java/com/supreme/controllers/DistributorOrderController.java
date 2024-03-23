package com.supreme.controllers;

import com.supreme.payload.request.OrderModel;
import com.supreme.serviceImpl.DistributorOrderServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/order/distributor")
public class DistributorOrderController {
    private final DistributorOrderServiceImpl distributorOrderServiceImpl;

    @Autowired
    public DistributorOrderController(DistributorOrderServiceImpl distributorOrderServiceImpl) {
        this.distributorOrderServiceImpl = distributorOrderServiceImpl;
    }

    // Create Distributor Order
    @PostMapping(path = "/addOrder", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addOrder(@ModelAttribute @Valid OrderModel orderRequest) {
        return distributorOrderServiceImpl.addOrder(orderRequest);
    }

    // Fetch Distributor order by order id
    @GetMapping("/getOrder/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        return distributorOrderServiceImpl.getOrder(orderId);
    }

    // Fetch List of Distributor orders
    @GetMapping("/getOrders/{distributorId}")
    public ResponseEntity<?> getDistributorOrders(@PathVariable Long distributorId) {
        return distributorOrderServiceImpl.getDistributorOrders(distributorId);
    }

    // Fetch Distributor order image
    @GetMapping("/pic/download/{orderImgName}") // Image fileName
    public ResponseEntity<?> getOrderPic(@PathVariable String orderImgName) throws IOException {
        return distributorOrderServiceImpl.getOrderPic(orderImgName);
    }


}
