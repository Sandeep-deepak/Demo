package com.supreme.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supreme.entity.*;
import com.supreme.payload.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ProfileUtility {

    private final ObjectMapper objectMapper;
    @Value("${path.distributor}")
    private String distributorPath;
    @Value("${path.executive}")
    private String executivePath;

    @Autowired
    public ProfileUtility(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Generate Image Download URL for Response
    public String generateDownloadUrl(String path, String name) { // name or mobileNumber
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(path)
                .path(name)
                .toUriString();
    }

    public String generateDownloadUrl(String path, String name, String picName, String picUrl) { // name or mobileNumber
        String downloadUrl = null;
        if (picName != null && picUrl != null) {
            downloadUrl = generateDownloadUrl(path, name);
        }
        return downloadUrl;
    }

    //    public <T> String convertListToJsonString(List<T> productItems) {
//        try {
//            return objectMapper.writeValueAsString(productItems);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Error converting product items to JSON", e);
//        }
//    }
//
//    public <T> List<T> convertJsonStringToList(String json, Class<T> targetType) {
//        try {
//            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, targetType));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Error converting JSON to list of objects", e);
//        }
//    }
//
    public List<DistributorProfileRespo> mapDistributorProfiles(List<DistributorProfile> profileList, String path) {
        return profileList.stream()
//                .map(this::mapDistributorProfile) // or
                .map(profile -> this.mapDistributorProfile(profile, generateDownloadUrl(path, profile.getMobileNumber(), profile.getProfilePicName(), profile.getProfilePicUrl())))
                .toList();
    }

    public List<ProductDTO> mapDistributorProductQuantityToProductDTO(List<DistributorProductQuantity> distributorProductQuantities) {
        List<ProductDTO> updatedProductsDTOList = new ArrayList<>();
        for (DistributorProductQuantity product : distributorProductQuantities) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(product.getProduct().getProductId());
            productDTO.setProductName(product.getProduct().getProductName());
            productDTO.setProductImgName(product.getProduct().getProductImgName());
            productDTO.setProductImgUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path(product.getProduct().getProductImgUrl()).toUriString());
            productDTO.setCurrentQuantity(product.getCurrentQty());
            updatedProductsDTOList.add(productDTO);
        }
        return updatedProductsDTOList;
    }

    public DistributorProfileRespo mapDistributorProfile(DistributorProfile profile, String downloadUrl) {
        DistributorProfileRespo profileResponse = new DistributorProfileRespo();

        profileResponse.setDistributorId(profile.getId());
        profileResponse.setFirstName(profile.getFirstName());
        profileResponse.setLastName(profile.getLastName());
        profileResponse.setPin(profile.getUser().getPin());
        profileResponse.setRole(profile.getUser().getRole());
        profileResponse.setMobileNumber(profile.getMobileNumber());
        profileResponse.setActive(profile.isActive());
        profileResponse.setDeleted(profile.isDeleted());
        profileResponse.setProfilePicName(profile.getProfilePicName());
        profileResponse.setProfilePicUrl(downloadUrl);

        if (profile.getDistributorProductQuantities() != null) {
            List<ProductDTO> productDTOList = mapDistributorProductQuantityToProductDTO(profile.getDistributorProductQuantities());
            productDTOList.stream()
                    .filter(productDTO -> productDTO.getProductImgName() != null && productDTO.getProductImgUrl() != null)
                    .forEach(productDTO -> productDTO.setProductImgUrl(generateDownloadUrl("/admin/product/download/", productDTO.getProductName())));
            profileResponse.setProducts(productDTOList);
        }

        profileResponse.setExecutiveProfiles(this.mapExecutiveProfiles(profile.getExecutiveProfiles(), executivePath));
        return profileResponse;
    }


    public DistributorProfileRespo mapDistributorProfileDB(DistributorProfile profile, List<ProductDTO> updatedDBProductsList, String downloadUrl) {
        DistributorProfileRespo profileResponse = new DistributorProfileRespo();

        profileResponse.setDistributorId(profile.getId());
        profileResponse.setFirstName(profile.getFirstName());
        profileResponse.setLastName(profile.getLastName());
        profileResponse.setPin(profile.getUser().getPin());
        profileResponse.setRole(profile.getUser().getRole());
        profileResponse.setMobileNumber(profile.getMobileNumber());
        profileResponse.setActive(profile.isActive());
        profileResponse.setDeleted(profile.isDeleted());
        profileResponse.setProfilePicName(profile.getProfilePicName());
        profileResponse.setProfilePicUrl(downloadUrl);
        if (profile.getDistributorProductQuantities() != null) {
            profileResponse.setProducts(updatedDBProductsList);
        }
        profileResponse.setExecutiveProfiles(this.mapExecutiveProfiles(profile.getExecutiveProfiles(), executivePath));
        return profileResponse;
    }

    public List<ExecutiveProfileResponse> mapExecutiveProfiles(List<ExecutiveProfile> profileList, String path) {
        return profileList.stream()
                .map(profile -> this.mapExecutiveProfile(profile, generateDownloadUrl(path, profile.getMobileNumber(), profile.getProfilePicName(), profile.getProfilePicUrl())))
                .toList();
    }

    public ExecutiveProfileResponse mapExecutiveProfile(ExecutiveProfile profile, String downloadUrl) {
        ExecutiveProfileResponse profileResponse = new ExecutiveProfileResponse();

        profileResponse.setDistributorId(profile.getDistributorProfile().getId());
        profileResponse.setDistributorName(profile.getDistributorProfile().getFirstName() + " " + profile.getDistributorProfile().getLastName());
        profileResponse.setExecutiveId(profile.getId());
        profileResponse.setFirstName(profile.getFirstName());
        profileResponse.setLastName(profile.getLastName());
        profileResponse.setMobileNumber(profile.getMobileNumber());
//        profileResponse.setPin(profile.getUser().getPin());
        profileResponse.setRole(profile.getUser().getRole());
        profileResponse.setActive(profile.isActive());
        profileResponse.setDeleted(profile.isDeleted());
        profileResponse.setProfilePicName(profile.getProfilePicName());
        profileResponse.setProfilePicUrl(downloadUrl);
        return profileResponse;
    }

    public List<DistributorOrderResponse> mapDistributorOrders(List<Order> orders) {
        return orders.stream()
                .map(this::mapDistributorOrderResponse) // or .map(order -> this.mapDistributorOrderResponse(order))
                .toList();
    }

    public DistributorOrderResponse mapDistributorOrderResponse(Order order) { //, String downloadOrderImgUrl
        DistributorOrderResponse distributorOrderResponse = new DistributorOrderResponse();

        distributorOrderResponse.setDistributorId(order.getDistributorProfile().getId());
        distributorOrderResponse.setDistributorName(order.getDistributorProfile().getFirstName() + " " + order.getDistributorProfile().getLastName());
        distributorOrderResponse.setRole(order.getDistributorProfile().getUser().getRole());

        distributorOrderResponse.setOutletId(order.getOutlet().getOutletId());
        distributorOrderResponse.setOutletName(order.getOutlet().getOutletName());

        distributorOrderResponse.setOrderId(order.getOrderId());
        distributorOrderResponse.setDateOfOrder(order.getDateOfOrder());
        distributorOrderResponse.setOrderImgName(order.getOrderImgName());
        distributorOrderResponse.setOrderImgUrl(generateDownloadUrl("/order/distributor/pic/download/", order.getOrderImgName()));
        distributorOrderResponse.setOrderLongitude(order.getOrderLongitude());
        distributorOrderResponse.setOrderLatitude(order.getOrderLatitude());

        distributorOrderResponse.setProducts(getOrderProductDTOS(order));

        return distributorOrderResponse;
    }

    public List<ExecutiveOrderResponse> mapExecutiveOrders(List<Order> orders) {
        return orders.stream()
                .map(this::mapExecutiveOrderResponse) // or .map(order -> this.mapDistributorOrderResponse(order))
                .toList();
    }

    public ExecutiveOrderResponse mapExecutiveOrderResponse(Order order) {
        ExecutiveOrderResponse executiveOrderResponse = new ExecutiveOrderResponse();

        executiveOrderResponse.setExecutiveId(order.getExecutiveProfile().getId());
        executiveOrderResponse.setExecutiveName(order.getExecutiveProfile().getFirstName() + " " + order.getExecutiveProfile().getLastName());
        executiveOrderResponse.setRole(order.getExecutiveProfile().getUser().getRole());

        executiveOrderResponse.setOutletId(order.getOutlet().getOutletId());
        executiveOrderResponse.setOutletName(order.getOutlet().getOutletName());

        executiveOrderResponse.setOrderId(order.getOrderId());
        executiveOrderResponse.setDateOfOrder(order.getDateOfOrder());
        executiveOrderResponse.setOrderImgName(order.getOrderImgName());
        executiveOrderResponse.setOrderImgUrl(generateDownloadUrl("/order/executive/pic/download/", order.getOrderImgName()));
        executiveOrderResponse.setOrderLongitude(order.getOrderLongitude());
        executiveOrderResponse.setOrderLatitude(order.getOrderLatitude());

        executiveOrderResponse.setProducts(getOrderProductDTOS(order));

        return executiveOrderResponse;
    }

    private List<OrderProductDTO> getOrderProductDTOS(Order order) {
        List<OrderProductDTO> orderProductDTOList = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            OrderProductDTO orderProductDTO = new OrderProductDTO();
            orderProductDTO.setProductId(orderItem.getProduct().getProductId());
            orderProductDTO.setProductName(orderItem.getProduct().getProductName());
            if (orderItem.getProduct().getProductImgName() != null && orderItem.getProduct().getProductImgUrl() != null) {
                orderProductDTO.setProductImgName(orderItem.getProduct().getProductImgName());
                orderProductDTO.setProductImgUrl(generateDownloadUrl("/admin/product/download/", orderItem.getProduct().getProductName()));
            }
            orderProductDTO.setOrderQuantity(orderItem.getQuantity());
            orderProductDTOList.add(orderProductDTO);
        }
        return orderProductDTOList;
    }

}
