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
@ToString
@Entity
@Table(name = "executiveProfile")
public class ExecutiveProfile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "executive_id")
	private Long id; //executiveId

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


	@OneToOne(mappedBy = "executiveProfile")
	@JsonIgnore
	private User user;

	@ManyToOne
	@JoinColumn(name = "distributor_id", nullable = false)
	@JsonIgnore // Ignore serialization of the author field to prevent infinite recursion
	private DistributorProfile distributorProfile;

	@OneToMany(mappedBy = "executiveProfile", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Order> orders = new ArrayList<>();

	public ExecutiveProfile(Long id, String firstName, String lastName, String mobileNumber, boolean active, boolean deleted, String profilePicName, String profilePicUrl, DistributorProfile distributorProfile) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mobileNumber = mobileNumber;
		this.active = active;
		this.deleted = deleted;
		this.profilePicName = profilePicName;
		this.profilePicUrl = profilePicUrl;
		this.distributorProfile = distributorProfile;
	}

}
