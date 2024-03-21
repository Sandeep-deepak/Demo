package com.supreme.services;

import com.supreme.entity.DistributorProfile;
import com.supreme.entity.Order;
import com.supreme.entity.Outlet;
import com.supreme.payload.request.OrderModel;
import com.supreme.payload.response.*;
import com.supreme.repository.*;
import com.supreme.utility.ProfileUtility;
import com.supreme.utility.S3Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DistributorOrderService {

    private final ProfileUtility profileUtility;
    private final S3Util s3Util;
    private final DistributorProfileRepo distributorRepo;
    //    private final AppFeaturesRepo appFeaturesRepo;
    private final Response response;
    private final ErrorResponse errorResponse;
    private final OrderResponse orderResponse;
    private final ProductService productService;
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
    @Value("${path.orderPath}")
    private String orderPath;

    @Autowired
    public DistributorOrderService(ProfileUtility profileUtility, S3Util s3Util, DistributorProfileRepo distributorRepo, Response response, ErrorResponse errorResponse, OrderResponse orderResponse, ProductService productService, OutletRepo outletRepo, OrderRepo orderRepo) {
        this.profileUtility = profileUtility;
        this.s3Util = s3Util;
        this.distributorRepo = distributorRepo;
        this.response = response;
        this.errorResponse = errorResponse;
        this.orderResponse = orderResponse;
        this.productService = productService;
        this.outletRepo = outletRepo;
        this.orderRepo = orderRepo;
    }

    // Creating Distributor Order
    public ResponseEntity<?> addOrder(OrderModel orderRequest) {
        DistributorProfile distributor = null;
        if (orderRequest.getDistributorId() != null) { // && orderRequest.getExecutiveId()==null
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
        if (orderRequest.getDistributorId() != null) { // && orderRequest.getExecutiveId()==null
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

        String downloadUrl = null;
        String downloadUri = orderPath + orderRequest.getOutletId();
        MultipartFile file = orderRequest.getOrderImage();
        if (file != null && !file.isEmpty()) {
            String s3Url = s3Util.uploadFile(s3FolderName, file);
            String fileName = s3Url.substring(s3Url.lastIndexOf("/") + 1);
            order.setOrderImgName(fileName);
            order.setOrderImgUrl(downloadUri);
            downloadUrl = profileUtility.generateDownloadUrl(orderPath, fileName);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Please upload order image", "MSG25"));
        }
        order.setOrderLatitude(orderRequest.getOrderLatitude());
        order.setOrderLongitude(orderRequest.getOrderLongitude());

        List<ProductDTO> dBProductsList = profileUtility.convertJsonStringToList(distributor.getProductsJson(), ProductDTO.class);
        List<ProductDTO> updatedDBProductsList = new ArrayList<>();
        for (ProductDTO product : dBProductsList) {

            switch (product.getProductName()) {
                case "Pan Masala":
                    if (orderRequest.getPanMasalaQty() != null) {
                        // currentQty = currentQty - orderQty
                        product.setCurrentQuantity(product.getCurrentQuantity() - orderRequest.getPanMasalaQty());
                    }
                    break;
                case "Coriander Powder":
                    if (orderRequest.getCorianderQty() != null) {
                        product.setCurrentQuantity(product.getCurrentQuantity() - orderRequest.getCorianderQty());
                    }
                    break;
                case "Mutton Masala":
                    if (orderRequest.getMuttonMasalaQty() != null) {
                        product.setCurrentQuantity(product.getCurrentQuantity() - orderRequest.getMuttonMasalaQty());
                    }
                    break;
                case "Chicken Masala":
                    if (orderRequest.getChickenMasalaQty() != null) {
                        product.setCurrentQuantity(product.getCurrentQuantity() - orderRequest.getChickenMasalaQty());
                    }
                    break;
                case "Chilli Powder":
                    if (orderRequest.getChilliPowderQty() != null) {
                        product.setCurrentQuantity(product.getCurrentQuantity() - orderRequest.getChilliPowderQty());
                    }
                    break;
                default:
                    // Default behavior if product name doesn't match any condition
                    break;
            }
            updatedDBProductsList.add(product);
        }
        String jsonString = profileUtility.convertListToJsonString(updatedDBProductsList);

        distributor.setProductsJson(jsonString);

        List<OrderProductDTO> orderProducts = profileUtility.mapOrderProductsToDTOs(updatedDBProductsList, orderRequest.getPanMasalaQty(), orderRequest.getCorianderQty(), orderRequest.getMuttonMasalaQty(), orderRequest.getChickenMasalaQty(), orderRequest.getChilliPowderQty());
        String orderProductsJson = profileUtility.convertListToJsonString(orderProducts);
        order.setProductsJson(orderProductsJson);

        orderRepo.save(order);
        distributorRepo.save(distributor);

        OrderResponse orderResponseUtil = profileUtility.mapOrderResponse(order, downloadUrl);
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setStatus(1);
        response.setMessageCode("MSG1");
        response.setMessage("Order  added successfully");
        response.setResult(orderResponseUtil);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}