package com.supreme.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "distributorProfile")
public class DistributorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "distributor_id")
    private Long id; //distributorId

    private String firstName;

    private String lastName;

    @NotNull(message = "Mobile Number must not be null")
    private String mobileNumber;
    @NotNull
    private boolean active;

    private boolean deleted;

    private String profilePicName;

    @Column(name = "profilePicUri")
    private String profilePicUrl;

    @Column(columnDefinition = "json")
    private String productsJson;
//    private int panMasalaQty; private int corianderQty; private int muttonMasalaQty; private int chickenMasalaQty;

    @OneToOne(mappedBy = "distributorProfile")
    @JsonIgnore
    private User user;

//    @OneToMany(mappedBy = "distributorProfile")
//    private List<DistributorProducts> distributorProducts;

    @OneToMany(mappedBy = "distributorProfile", cascade = CascadeType.ALL)
    @JsonIgnore // Ignore serialization of the author field to prevent infinite recursion
    private List<ExecutiveProfile> executiveProfiles = new ArrayList<>();

    @OneToMany(mappedBy = "distributorProfile", cascade = CascadeType.ALL)
    @JsonIgnore // Ignore serialization of the author field to prevent infinite recursion
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "distributorProfile", cascade = CascadeType.ALL)
    @JsonIgnore // Ignore serialization of the author field to prevent infinite recursion
    private List<ProductsNewQty> productsNewQties = new ArrayList<>();

    public DistributorProfile(Long id, String firstName, String lastName, String mobileNumber, boolean active, boolean deleted, String profilePicName, String profilePicUrl) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.active = active;
        this.deleted = deleted;
        this.profilePicName = profilePicName;
        this.profilePicUrl = profilePicUrl;
    }

}
