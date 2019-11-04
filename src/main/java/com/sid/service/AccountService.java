package com.sid.service;

import java.io.IOException;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.servlet.ModelAndView;

import com.google.zxing.WriterException;
import com.nexmo.client.NexmoClientException;
import com.sid.entities.User;

public interface AccountService {
	static String NEXMO_API_KEY = "nexmo_api_key";
	static String NEXMO_API_SECRET = "nexmo_api_secret";
	
	public boolean isAgent(String phoneNumber);
	
	public boolean isEmailValide(String email);
	
	public byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException ;
		
	public int createUserPhoneNumber(String phoneNumber) ;
	
	public String startPhoneNumberVerification(String phoneNumber);
	
	public String checkPhoneNumberVerification(String requestId, String code) throws NexmoClientException, IOException;
	
	public void sendEmail(SimpleMailMessage email);
	
	public ModelAndView verifyEmail(User user);
	
	public User getUserByPhoneNumber(String phoneNumber);
		
	
	public User saveUserInformations(String phoneNumber, String lastName, String surname, String email, String role);

	public boolean PhoneNumberValidation(String phoneNumber);

	public void userTokenCreation(String phoneNumber);

	boolean tokenVerification(String phoneNumber, String token);
}
