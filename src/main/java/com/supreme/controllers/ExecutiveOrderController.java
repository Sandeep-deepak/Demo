package com.supreme.controllers;

import com.supreme.payload.request.OrderModel;
import com.supreme.serviceImpl.ExecutiveOrderServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/order/executive")
public class ExecutiveOrderController {
    private final ExecutiveOrderServiceImpl executiveOrderServiceImpl;

    @Autowired
    public ExecutiveOrderController(ExecutiveOrderServiceImpl executiveOrderServiceImpl) {
        this.executiveOrderServiceImpl = executiveOrderServiceImpl;
    }

    // Create Executive Order
    @PostMapping(path = "/addOrder", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addOrder(@ModelAttribute @Valid OrderModel orderRequest) {
        return executiveOrderServiceImpl.addOrder(orderRequest);
    }

    // Fetch Executive order by order id
    @GetMapping("/getOrder/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        return executiveOrderServiceImpl.getOrder(orderId);
    }

    // Fetch List of Executive orders
    @GetMapping("/getOrders/{executiveId}")
    public ResponseEntity<?> getExecutiveOrders(@PathVariable Long executiveId) {
        return executiveOrderServiceImpl.getExecutiveOrders(executiveId);
    }

    // Fetch Executive order image
    @GetMapping("/pic/download/{orderImgName}") // Image fileName  orderImgName
    public ResponseEntity<?> getOrderPic(@PathVariable String orderImgName) throws IOException {
        return executiveOrderServiceImpl.getOrderPic(orderImgName);
    }

}
