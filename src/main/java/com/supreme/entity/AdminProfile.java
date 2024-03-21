package com.supreme.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "adminProfile")
public class AdminProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    private String firstName;

    private String lastName;

    @NotNull(message = "Mobile Number must not be null")
    private String mobileNumber;

    @OneToOne(mappedBy = "adminProfile")
    private User user;

//    private String profilePicName;

//    @Column(name = "profilePicUri")
//    private String profilePicUrl;

    public AdminProfile(Long adminId, String firstName, String lastName, String mobileNumber) {
        this.adminId = adminId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
    }
}
