package com.sid.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sid.entities.ConfirmationOTP;

public interface ConfirmationOTPRepository extends JpaRepository<ConfirmationOTP, String> {
		@Query("SELECT ct FROM ConfirmationOTP ct WHERE ct.confirmationOTP = :token")
	    ConfirmationOTP findByConfirmationToken(@Param("token") String confirmationToken);
	
}
