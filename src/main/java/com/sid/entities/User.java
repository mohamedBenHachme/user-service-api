package com.sid.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.lang.Nullable;

@Entity
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long userId;
	@Column(unique = true)
	private String phoneNumber;
	@Nullable
	private String userToken;
	@Column(name="lastName")
	@Nullable
	private String lastName;
	@Nullable
	private String surname;
	@Nullable
	private String email;
	@Column(name="qrCode")
	@Nullable
	private byte[] qrCode;
	@Column(name="idCard")
	@Nullable
	private byte[] idCard;
	private String role = "Customer";
	private boolean isEnabled = false;
	private boolean isIDCecked = false;
	private boolean isEmailConfirmed = false;
	

	public void setIDCecked(boolean isIDCecked) {
		this.isIDCecked = isIDCecked;
	}

	public void setEmailConfirmed(boolean isEmailConfirmed) {
		this.isEmailConfirmed = isEmailConfirmed;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	public void setQrCode( byte[] qrCode) {
		this.qrCode = qrCode;
	}
	public byte[] getQrCode() {
		return this.qrCode;
	}
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public byte[] getIdCard() {
		return idCard;
	}

	public long getUserId() {
		return userId;
	}

	public void setIdCard(byte[] idCard) {
		this.idCard = idCard;
	}
	public boolean isEnabled() {
		return isEnabled;
	}

	public boolean isIDCecked() {
		return isIDCecked;
	}

	public boolean isEmailConfirmed() {
		return isEmailConfirmed;
	}	
	public User() {
		// TODO Auto-generated constructor stub
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

}
