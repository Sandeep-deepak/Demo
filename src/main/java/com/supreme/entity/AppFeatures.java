package com.supreme.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "appFeatures")
public class AppFeatures {
    @Id
    @JsonIgnore
    private Long appFeaturesId;

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
