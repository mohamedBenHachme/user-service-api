package com.sid.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sid.entities.User;

public interface UserRepository extends JpaRepository<User, String> {
	

	
	
	
	
	
	@Query("SELECT u FROM User u WHERE u.phoneNumber = :phone")
	public User getByPhoneNumber( @Param("phone") String phone);
	
	@Query("SELECT u.role FROM User u WHERE u.phoneNumber = :phone")
	public String getUserRole(@Param("phone") String phone);
	
	@Query("SELECT u FROM User u WHERE u.userId = :id")
	public User getUserById(@Param("id") long id);
}
