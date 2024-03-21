package com.supreme.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private LocalDateTime dateOfOrder;

    @Column(columnDefinition = "json")
    private String productsJson;

    private String orderImgName;
    @Column(name = "orderImgUri")
    private String orderImgUrl;
    private String orderLongitude;
    private String orderLatitude;

    @ManyToOne
    @JoinColumn(name = "outlet_id", nullable = false)
    @JsonIgnore
    private Outlet outlet;

    @ManyToOne
    @JoinColumn(name = "distributor_id")
    @JsonIgnore
    private DistributorProfile distributorProfile;

    @ManyToOne
    @JoinColumn(name = "executive_id")
    @JsonIgnore
    private ExecutiveProfile executiveProfile;
}
