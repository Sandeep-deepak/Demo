package com.supreme.entity;

import jakarta.persistence.*;
import lombok.*;

//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "distributorProductQuantity")
public class DistributorProductQuantity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "distributor_id")
    private DistributorProfile distributorProfile;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int currentQty;

    // For Dummy Data loader
    public DistributorProductQuantity(DistributorProfile distributorProfile, Product product, int currentQty) {
        this.distributorProfile = distributorProfile;
        this.product = product;
        this.currentQty = currentQty;
    }
}
