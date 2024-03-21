package com.supreme.controllers;

import com.supreme.payload.request.OrderModel;
import com.supreme.services.DistributorOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final DistributorOrderService distributorOrderService;

    @Autowired
    public OrderController(DistributorOrderService distributorOrderService) {
        this.distributorOrderService = distributorOrderService;
    }

    // Creating Order
    @PostMapping(path = "/addOrder", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addOrder(@ModelAttribute @Valid OrderModel orderRequest) {
        return distributorOrderService.addOrder(orderRequest);
    }


//    @GetMapping("/getOrder/{phNo}")
//    public ResponseEntity<?> getOrder(@PathVariable String phNo) {
//        return distributorOrderService.getOrder(phNo);
//    }
//
//    @GetMapping("/ordersByDate")
//    public ResponseEntity<?> getOrdersByDate(@RequestParam(required = false) LocalDateTime fromDate, @RequestParam(required = false) LocalDateTime toDate) {
//        return distributorOrderService.getOrdersByDate(fromDate, toDate);
//    }
//
//    // Get Order Picture
//    @GetMapping("/pic/download/{orderId}") // Image fileName
//    public ResponseEntity<?> getOrderPic(@PathVariable Long orderId) throws IOException {
//        return distributorOrderService.getOrderPic(orderId);
//    }
}


