package com.example.demo.model.entity.user;

import java.sql.Timestamp;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "user_pswd_hash")
	private String userPwdHash;


	@Column(name = "user_phone")
	private String userPhone;

	@Column(name = "user_email")
	private String userEmail;

	@Column(name = "user_idcard")
	private String userIdCard;
	
	@Column(name ="user_birth_date")
	private LocalDate userBirthDate;
	

	@Column(name = "user_regdate" ,insertable = false, updatable = false)
	private Timestamp userRegdate;

	@Column(name = "user_is_verified")
	private Boolean userIsVerified;

}
