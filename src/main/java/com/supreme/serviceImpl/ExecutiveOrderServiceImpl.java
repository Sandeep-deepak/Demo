package com.supreme.serviceImpl;

import com.supreme.entity.*;
import com.supreme.payload.request.OrderModel;
import com.supreme.payload.response.ErrorResponse;
import com.supreme.payload.response.ExecutiveOrderResponse;
import com.supreme.payload.response.Response;
import com.supreme.repository.*;
import com.supreme.service.ExecutiveOrderService;
import com.supreme.utility.ProfileUtility;
import com.supreme.utility.S3Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExecutiveOrderServiceImpl implements ExecutiveOrderService {

    private final ProfileUtility profileUtility;
    private final S3Util s3Util;
    private final ExecutiveProfileRepo executiveProfileRepo;
    private final DistributorProductQtyRepo distributorProductQtyRepo;
    private final OrderItemRepo orderItemRepo;
    //    private final AppFeaturesRepo appFeaturesRepo;
    private final Response response;
    private final ErrorResponse errorResponse;
    private final OutletRepo outletRepo;
    private final OrderRepo orderRepo;
    @Value("${s3.outletOrderPic}")
    private String s3FolderName;
    @Value("${path.distributor}")
    private String distributorPath;
    @Value("${path.executive}")
    private String executivePath;
    @Value("${path.outletPath}")
    private String outletPath;
    @Value("${path.executiveOrderPath}")
    private String executiveOrderPath;

    @Autowired
    public ExecutiveOrderServiceImpl(ProfileUtility profileUtility, S3Util s3Util, ExecutiveProfileRepo executiveProfileRepo, DistributorProductQtyRepo distributorProductQtyRepo, OrderItemRepo orderItemRepo, Response response, ErrorResponse errorResponse, OutletRepo outletRepo, OrderRepo orderRepo) {
        this.profileUtility = profileUtility;
        this.s3Util = s3Util;
        this.executiveProfileRepo = executiveProfileRepo;
        this.distributorProductQtyRepo = distributorProductQtyRepo;
        this.orderItemRepo = orderItemRepo;
        this.response = response;
        this.errorResponse = errorResponse;
        this.outletRepo = outletRepo;
        this.orderRepo = orderRepo;
    }

    // Create Executive Order
    @Override
    public ResponseEntity<?> addOrder(OrderModel orderRequest) {
        ExecutiveProfile executiveProfile = null;
        if (orderRequest.getExecutiveId() != null) {
            Optional<ExecutiveProfile> executiveProfileOptional = executiveProfileRepo.findById(orderRequest.getExecutiveId());
            if (executiveProfileOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Executive Profile does not exists", "MSG25"));
            }
            executiveProfile = executiveProfileOptional.get();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Pass Executive Profile Id", "MSG25"));
        }

        Outlet outlet = null;
        if (orderRequest.getOutletId() != null) { // && orderRequest.getExecutiveId()==null
            Optional<Outlet> outletOptional = outletRepo.findById(orderRequest.getOutletId());
            if (outletOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Outlet does not exists", "MSG25"));
            }
            outlet = outletOptional.get();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Pass Outlet Id", "MSG25"));
        }

        Order order = new Order();
        order.setExecutiveProfile(executiveProfile);
//        order.setDistributorProfile(null);
        order.setOutlet(outlet);
        order.setDateOfOrder(LocalDateTime.now());

        MultipartFile file = orderRequest.getOrderImage();
        if (file != null && !file.isEmpty()) {
            String s3Url = s3Util.uploadFile(s3FolderName, file);
            String fileName = s3Url.substring(s3Url.lastIndexOf("/") + 1);
            order.setOrderImgName(fileName);
            order.setOrderImgUrl(executiveOrderPath + fileName);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Please upload order image", "MSG25"));
        }
        order.setOrderLatitude(orderRequest.getOrderLatitude());
        order.setOrderLongitude(orderRequest.getOrderLongitude());

        List<OrderItem> orderItems = new ArrayList<>();
        for (DistributorProductQuantity distProductQty : distributorProductQtyRepo.findByDistributorProfileId(executiveProfile.getDistributorProfile().getId())) {
            OrderItem orderItem = new OrderItem();

            switch (distProductQty.getProduct().getProductName()) {
                case "Pan Masala":
                    if (orderRequest.getPanMasalaQty() != null) {
                        orderItem.setProduct(distProductQty.getProduct());
                        orderItem.setQuantity(orderRequest.getPanMasalaQty());
                        distProductQty.setCurrentQty(distProductQty.getCurrentQty() - orderRequest.getPanMasalaQty());
                    }
                    break;
                case "Coriander Powder":
                    if (orderRequest.getCorianderQty() != null) {
                        orderItem.setProduct(distProductQty.getProduct());
                        orderItem.setQuantity(orderRequest.getCorianderQty());
                        distProductQty.setCurrentQty(distProductQty.getCurrentQty() - orderRequest.getCorianderQty());
                    }
                    break;
                case "Mutton Masala":
                    if (orderRequest.getMuttonMasalaQty() != null) {
                        orderItem.setProduct(distProductQty.getProduct());
                        orderItem.setQuantity(orderRequest.getMuttonMasalaQty());
                        distProductQty.setCurrentQty(distProductQty.getCurrentQty() - orderRequest.getMuttonMasalaQty());
                    }
                    break;
                case "Chicken Masala":
                    if (orderRequest.getChickenMasalaQty() != null) {
                        orderItem.setProduct(distProductQty.getProduct());
                        orderItem.setQuantity(orderRequest.getChickenMasalaQty());
                        distProductQty.setCurrentQty(distProductQty.getCurrentQty() - orderRequest.getChickenMasalaQty());
                    }
                    break;
                case "Chilli Powder":
                    if (orderRequest.getChilliPowderQty() != null) {
                        orderItem.setProduct(distProductQty.getProduct());
                        orderItem.setQuantity(orderRequest.getChilliPowderQty());
                        distProductQty.setCurrentQty(distProductQty.getCurrentQty() - orderRequest.getChilliPowderQty());
                    }
                    break;
                default:
                    break;
            }
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        orderRepo.save(order);

        orderItemRepo.saveAll(orderItems);

        ExecutiveOrderResponse executiveOrderResponseUtil = profileUtility.mapExecutiveOrderResponse(order);
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setStatus(1);
        response.setMessageCode("MSG1");
        response.setMessage("Executive Order added successfully");
        response.setResult(executiveOrderResponseUtil);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Fetch Executive order by order id
    @Override
    public ResponseEntity<?> getOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepo.findById(orderId);
        if (orderOptional.isEmpty()) {
            errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            errorResponse.setStatus(0);
            errorResponse.setMessageCode("MSG48");
            errorResponse.setMessage("Executive Order Doesn't exists");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        Order order = orderOptional.get();

        ExecutiveOrderResponse executiveOrderResponseUtil = profileUtility.mapExecutiveOrderResponse(order);
        response.setStatusCode(HttpStatus.OK.value());
        response.setStatus(1);
        response.setMessageCode("MSG17");
        response.setMessage("Executive Order fetched successfully");
        response.setResult(executiveOrderResponseUtil);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Fetch List of Executive orders
    @Override
    public ResponseEntity<?> getExecutiveOrders(Long executiveId) {
        Optional<ExecutiveProfile> executiveProfileOptional = executiveProfileRepo.findById(executiveId);
        if (executiveProfileOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Executive Profile does not exists", "MSG25"));
        }
        ExecutiveProfile executive = executiveProfileOptional.get();

        List<ExecutiveOrderResponse> executiveOrderResponseList = profileUtility.mapExecutiveOrders(executive.getOrders());
        response.setStatusCode(HttpStatus.OK.value());
        response.setStatus(1);
        response.setMessageCode("MSG17");
        response.setMessage("List of Executive Orders fetched successfully");
        response.setResult(executiveOrderResponseList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Fetch Executive order image
    @Override
    public ResponseEntity<?> getOrderPic(String orderImgName) throws IOException {
        Optional<Order> orderOptional = orderRepo.findByOrderImgName(orderImgName);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (order.getOrderImgName() != null && order.getOrderImgUrl() != null) {
                return s3Util.getImageFromS3Bucket(s3FolderName, order.getOrderImgName());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 0, "Image Not Found", "MSG26"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 0, "Executive Order Doesn't exists", "MSG11"));
        }
    }

}
