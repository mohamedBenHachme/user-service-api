package com.sid.entities;

import java.util.Date;
import java.util.Random;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

 

@Entity
public class ConfirmationOTP {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long otpId;

    private String confirmationOTP;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
    public ConfirmationOTP() {
    	super();
    }
    public ConfirmationOTP(User user) {
        this.user = user;
        createdDate = new Date();
        confirmationOTP = generateOTP();
    }

	public long getOtpId() {
		return otpId;
	}

	public String getConfirmationOTP() {
		return confirmationOTP;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public User getUser() {
		return user;
	}

	public void setOtpId(long tokenid) {
		this.otpId = tokenid;
	}

	public void setConfirmationOTP(String confirmationToken) {
		this.confirmationOTP = confirmationToken;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setUser(User user) {
		this.user = user;
	}

	static public String generateOTP() {
		Random random = new Random(); 
		String generatePin = String.format("%04d", random.nextInt(10000000) % 10000); 
		return generatePin;
	}
}
