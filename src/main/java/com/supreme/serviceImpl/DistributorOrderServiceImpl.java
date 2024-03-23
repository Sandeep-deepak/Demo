package com.supreme.serviceImpl;

import com.supreme.entity.*;
import com.supreme.payload.request.OrderModel;
import com.supreme.payload.response.*;
import com.supreme.repository.*;
import com.supreme.service.DistributorOrderService;
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
public class DistributorOrderServiceImpl implements DistributorOrderService {

    private final ProfileUtility profileUtility;
    private final S3Util s3Util;
    private final DistributorProfileRepo distributorRepo;
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
    @Value("${path.distributorOrderPath}")
    private String distributorOrderPath;

    @Autowired
    public DistributorOrderServiceImpl(ProfileUtility profileUtility, S3Util s3Util, DistributorProfileRepo distributorRepo, DistributorProductQtyRepo distributorProductQtyRepo, OrderItemRepo orderItemRepo, Response response, ErrorResponse errorResponse, OutletRepo outletRepo, OrderRepo orderRepo) {
        this.profileUtility = profileUtility;
        this.s3Util = s3Util;
        this.distributorRepo = distributorRepo;
        this.distributorProductQtyRepo = distributorProductQtyRepo;
        this.orderItemRepo = orderItemRepo;
        this.response = response;
        this.errorResponse = errorResponse;
        this.outletRepo = outletRepo;
        this.orderRepo = orderRepo;
    }

    // Create Distributor Order
    @Override
    public ResponseEntity<?> addOrder(OrderModel orderRequest) {
        DistributorProfile distributor = null;
        if (orderRequest.getDistributorId() != null) {
            Optional<DistributorProfile> distributorProfileOptional = distributorRepo.findById(orderRequest.getDistributorId());
            if (distributorProfileOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Distributor Profile does not exists", "MSG25"));
            }
            distributor = distributorProfileOptional.get();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Pass Distributor Profile Id", "MSG25"));
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
        order.setDistributorProfile(distributor);
        order.setOutlet(outlet);
        order.setDateOfOrder(LocalDateTime.now());

//        String downloadUrl = null;
        MultipartFile file = orderRequest.getOrderImage();
        if (file != null && !file.isEmpty()) {
            String s3Url = s3Util.uploadFile(s3FolderName, file);
            String fileName = s3Url.substring(s3Url.lastIndexOf("/") + 1);
            order.setOrderImgName(fileName);
            order.setOrderImgUrl(distributorOrderPath + fileName);
//            downloadUrl = profileUtility.generateDownloadUrl(distributorOrderPath, fileName);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Please upload order image", "MSG25"));
        }
        order.setOrderLatitude(orderRequest.getOrderLatitude());
        order.setOrderLongitude(orderRequest.getOrderLongitude());

        List<OrderItem> orderItems = new ArrayList<>();
//        List<DistributorProductQuantity> updatedDBProducts = new ArrayList<>();
        for (DistributorProductQuantity distProductQty : distributorProductQtyRepo.findByDistributorProfileId(distributor.getId())) {
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
                    // Default behavior if product name doesn't match any condition
                    break;
            }
            orderItem.setOrder(order);
            orderItems.add(orderItem);
//            updatedDBProducts.add(distProductQty);
        }

        order.setOrderItems(orderItems);
        orderRepo.save(order);

        orderItemRepo.saveAll(orderItems);
//        distributorRepo.save(distributor);

        DistributorOrderResponse distributorOrderResponseUtil = profileUtility.mapDistributorOrderResponse(order);
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setStatus(1);
        response.setMessageCode("MSG1");
        response.setMessage("Distributor Order added successfully");
        response.setResult(distributorOrderResponseUtil);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Fetch Distributor order by order id
    @Override
    public ResponseEntity<?> getOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepo.findById(orderId);
        if (orderOptional.isEmpty()) {
            errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            errorResponse.setStatus(0);
            errorResponse.setMessageCode("MSG48");
            errorResponse.setMessage("Distributor Order Doesn't exists");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        Order order = orderOptional.get();
//        String downloadUrl = null;
//
//        if (order.getOrderImgName() != null && order.getOrderImgUrl() != null) {
//            downloadUrl = profileUtility.generateDownloadUrl(distributorOrderPath, order.getOrderImgName());
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 0, "Order Picture Not Found", "MSG27"));
//        }

        DistributorOrderResponse distributorOrderResponseUtil = profileUtility.mapDistributorOrderResponse(order); //, downloadUrl
        response.setStatusCode(HttpStatus.OK.value());
        response.setStatus(1);
        response.setMessageCode("MSG17");
        response.setMessage("Distributor Order fetched successfully");
        response.setResult(distributorOrderResponseUtil);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Fetch List of Distributor orders
    @Override
    public ResponseEntity<?> getDistributorOrders(Long distributorId) {
        Optional<DistributorProfile> distributorProfileOptional = distributorRepo.findById(distributorId);
        if (distributorProfileOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Distributor Profile does not exists", "MSG25"));
        }
        DistributorProfile distributor = distributorProfileOptional.get();

        List<DistributorOrderResponse> distributorOrderResponseUtil = profileUtility.mapDistributorOrders(distributor.getOrders());
        response.setStatusCode(HttpStatus.OK.value());
        response.setStatus(1);
        response.setMessageCode("MSG17");
        response.setMessage("List of Distributor Orders fetched successfully");
        response.setResult(distributorOrderResponseUtil);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Fetch Distributor order image
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 0, "Distributor Order Doesn't exists", "MSG11"));
        }
    }

}
