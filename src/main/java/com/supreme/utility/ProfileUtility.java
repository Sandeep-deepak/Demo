package com.supreme.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supreme.entity.DistributorProfile;
import com.supreme.entity.ExecutiveProfile;
import com.supreme.entity.Order;
import com.supreme.entity.Product;
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

    public <T> String convertListToJsonString(List<T> productItems) {
        try {
            return objectMapper.writeValueAsString(productItems);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting product items to JSON", e);
        }
    }

    public <T> List<T> convertJsonStringToList(String json, Class<T> targetType) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, targetType));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to list of objects", e);
        }
    }

    public List<DistributorProfileRespo> mapDistributorProfiles(List<DistributorProfile> profileList, String path) {
        return profileList.stream()
//                .map(this::mapDistributorProfile) // or
                .map(profile -> this.mapDistributorProfile(profile, generateDownloadUrl(path, profile.getMobileNumber(), profile.getProfilePicName(), profile.getProfilePicUrl())))
                .toList();
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
        if (profile.getProductsJson() != null) {
            List<ProductDTO> productDTOList = convertJsonStringToList(profile.getProductsJson(), ProductDTO.class);
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
        if (profile.getProductsJson() != null) {
            profileResponse.setProducts(updatedDBProductsList);
        }
        profileResponse.setExecutiveProfiles(this.mapExecutiveProfiles(profile.getExecutiveProfiles(), executivePath));
        return profileResponse;
    }


    public List<ExecutiveProfileResponse> mapExecutiveProfiles(List<ExecutiveProfile> profileList, String path) {
        return profileList.stream()
//                .map(this::mapDistributorProfile) // or
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

    // Used at creating distributor profile
    public List<ProductDTO> mapProductsToDTOs(List<Product> productList, Integer panMasalaQty, Integer corianderQty, Integer muttonMasalaQty, Integer chickenMasalaQty, Integer chilliPowderQty) {
        List<ProductDTO> productDTOList = new ArrayList<>();

        for (Product product : productList) { // productList = productService.getProductsList()
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductName(product.getProductName());
            productDTO.setProductId(product.getProductId());
            productDTO.setProductImgName(product.getProductImgName());
            productDTO.setProductImgUrl(product.getProductImgUrl());
            productDTO.setNewQuantity(0); // For all products initially

            // Set quantity based on product name
            switch (product.getProductName()) {
                case "Pan Masala":
                    if (panMasalaQty != null) {
                        productDTO.setCurrentQuantity(panMasalaQty); // productDTO.setQuantity(panMasalaQty);
                    }
                    break;
                case "Coriander Powder":
                    if (corianderQty != null) {
                        productDTO.setCurrentQuantity(corianderQty);
                    }
                    break;
                case "Mutton Masala":
                    if (muttonMasalaQty != null) {
                        productDTO.setCurrentQuantity(muttonMasalaQty);
                    }
                    break;
                case "Chicken Masala":
                    if (chickenMasalaQty != null) {
                        productDTO.setCurrentQuantity(chickenMasalaQty);
                    }
                    break;
                case "Chilli Powder":
                    if (chilliPowderQty != null) {
                        productDTO.setCurrentQuantity(chilliPowderQty);
                    }
                    break;
                default:
                    // Default behavior if product name doesn't match any condition
                    break;
            }
/*
            if (product.getProductName().equals("Pan Masala") && orderRequest.getPanMasalaQty() != null) {
                productDTO.setQuantity(orderRequest.getPanMasalaQty());
            }
            if (product.getProductName().equals("Coriander Powder") && orderRequest.getCorianderQty() != null) {
                productDTO.setQuantity(orderRequest.getCorianderQty());
            }
            if (product.getProductName().equals("Mutton Masala") && orderRequest.getMuttonMasalaQty() != null) {
                productDTO.setQuantity(orderRequest.getMuttonMasalaQty());
            }
            if (product.getProductName().equals("Chicken Masala") && orderRequest.getChickenMasalaQty() != null) {
                productDTO.setQuantity(orderRequest.getChickenMasalaQty());
            }
            if (product.getProductName().equals("Chilli Powder") && orderRequest.getChilliPowderQty() != null) {
                productDTO.setQuantity(orderRequest.getChilliPowderQty());
            }
*/
            productDTOList.add(productDTO);
        }
        return productDTOList;
    }

    public List<OrderProductDTO> mapOrderProductsToDTOs(List<ProductDTO> productList, Integer panMasalaQty, Integer corianderQty, Integer muttonMasalaQty, Integer chickenMasalaQty, Integer chilliPowderQty) {
        List<OrderProductDTO> orderProductDTOList = new ArrayList<>();

        for (ProductDTO product : productList) {
            OrderProductDTO orderProductDTO = new OrderProductDTO();
            orderProductDTO.setProductName(product.getProductName());
            orderProductDTO.setProductId(product.getProductId());
            orderProductDTO.setProductImgName(product.getProductImgName());
            orderProductDTO.setProductImgUrl(product.getProductImgUrl());

            // Set quantity based on product name
            switch (product.getProductName()) {
                case "Pan Masala":
                    if (panMasalaQty != null) {
                        orderProductDTO.setOrderQuantity(panMasalaQty);
                    }
                    break;
                case "Coriander Powder":
                    if (corianderQty != null) {
                        orderProductDTO.setOrderQuantity(corianderQty);
                    }
                    break;
                case "Mutton Masala":
                    if (muttonMasalaQty != null) {
                        orderProductDTO.setOrderQuantity(muttonMasalaQty);
                    }
                    break;
                case "Chicken Masala":
                    if (chickenMasalaQty != null) {
                        orderProductDTO.setOrderQuantity(chickenMasalaQty);
                    }
                    break;
                case "Chilli Powder":
                    if (chilliPowderQty != null) {
                        orderProductDTO.setOrderQuantity(chilliPowderQty);
                    }
                    break;
                default:
                    // Default behavior if product name doesn't match any condition
                    break;
            }
            orderProductDTOList.add(orderProductDTO);
        }
        return orderProductDTOList;
    }

    public OrderResponse mapOrderResponse(Order order, String downloadOrderImgUrl) {
        OrderResponse orderResponse = new OrderResponse();

        orderResponse.setOrderId(order.getOrderId());
        orderResponse.setDistributorId(order.getDistributorProfile().getId());
        orderResponse.setOutletId(order.getOutlet().getOutletId());
        orderResponse.setDateOfOrder(order.getDateOfOrder());

        orderResponse.setOrderImgName(order.getOrderImgName());
        orderResponse.setOrderImgUrl(downloadOrderImgUrl);
        orderResponse.setOrderLongitude(order.getOrderLongitude());
        orderResponse.setOrderLatitude(order.getOrderLatitude());
        orderResponse.setRole(order.getDistributorProfile().getUser().getRole());
        orderResponse.setFullName(order.getDistributorProfile().getFirstName() + " " + order.getDistributorProfile().getLastName());
        if (order.getProductsJson() != null) {
            List<OrderProductDTO> orderProductDTOList = convertJsonStringToList(order.getProductsJson(), OrderProductDTO.class);
            orderProductDTOList.stream()
                    .filter(productDTO -> productDTO.getProductImgName() != null && productDTO.getProductImgUrl() != null)
                    .forEach(productDTO -> productDTO.setProductImgUrl(generateDownloadUrl("/admin/product/download/", productDTO.getProductName())));
            orderResponse.setProducts(orderProductDTOList);
        }
        return orderResponse;
    }

}
