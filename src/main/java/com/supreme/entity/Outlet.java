package com.supreme.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
//@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "outlets")
public class Outlet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outletId;
    private String outletName;
    private String mobileNumber;
    private String outletAddress;
    private String outletImgName;
    @Column(name = "outletImgUri")
    private String outletImgUrl;

//    @OneToMany(mappedBy = "outlet", cascade = CascadeType.ALL)
//    @JsonIgnore
//    private List<Order> orders = new ArrayList<>();

    public Outlet(Long outletId, String outletName, String mobileNumber, String outletAddress, String outletImgName, String outletImgUrl) {
        this.outletId = outletId;
        this.outletName = outletName;
        this.mobileNumber = mobileNumber;
        this.outletAddress = outletAddress;
        this.outletImgName = outletImgName;
        this.outletImgUrl = outletImgUrl;
    }
}
