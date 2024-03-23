package com.supreme.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class    User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotNull
    private String mobileNumber;

    @NotBlank
    @NotNull
    private String pin;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ERole role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "adminProfile_id", referencedColumnName = "admin_id")
    @JsonIgnore
    private AdminProfile adminProfile;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "distributorProfile_id", referencedColumnName = "distributor_id")
    @JsonIgnore
    private DistributorProfile distributorProfile;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "executiveProfile_id", referencedColumnName = "executive_id")
    @JsonIgnore
    private ExecutiveProfile executiveProfile;


    public User(String mobileNumber, String pin) {
        this.mobileNumber = mobileNumber;
        this.pin = pin;
    }

    public User(String mobileNumber, String pin, ERole role) {
        this.mobileNumber = mobileNumber;
        this.pin = pin;
        this.role = role;
    }

    public User(Long id, String mobileNumber, String pin, ERole role) {
        this.id = id;
        this.mobileNumber = mobileNumber;
        this.pin = pin;
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", pin='" + pin + '\'' +
                ", role=" + role +
                '}';
    }
}
