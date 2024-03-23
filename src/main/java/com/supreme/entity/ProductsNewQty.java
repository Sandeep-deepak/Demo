package com.supreme.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "productsNewQty")
public class ProductsNewQty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(columnDefinition = "json")
//    private String productsJson;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int newQty = 0;
    private int currentQty = 0;

    private LocalDateTime addedDate;

    @ManyToOne
    @JoinColumn(name = "distributor_id", nullable = false)
    @JsonIgnore
    private DistributorProfile distributorProfile;

}
