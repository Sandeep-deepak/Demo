package com.supreme.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppFeaturesReq {

    private boolean showTnCAgreement;
    private boolean showSubCategories;
    private boolean showPrice;
    private boolean showDelivery;
    private boolean addCategory;
    private boolean updateCategory;
    private boolean deleteCategory;
    private boolean addSubCategory;
    private boolean updateSubCategory;
    private boolean deleteSubCategory;
    private boolean addProduct;
    private boolean updateProduct;
    private boolean deleteProduct;
    private boolean useLocalImages;
    private boolean profilePicSize1Mb;
    private boolean profilePicSize2Mb;

//    private boolean productApproval;
//    private boolean orderApproval;
//    private boolean coupon;
}
