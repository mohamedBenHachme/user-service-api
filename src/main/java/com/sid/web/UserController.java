package com.sid.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.nexmo.client.NexmoClientException;
import com.sid.dao.ConfirmationOTPRepository;
import com.sid.dao.UserRepository;
import com.sid.entities.ConfirmationOTP;
import com.sid.entities.User;
import com.sid.service.AccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.Data;

@RestController
@RequestMapping("/userservice")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private AccountService accountService;
	@Autowired
	private UserRepository userRepository;
	@Autowired 
	private ConfirmationOTPRepository confirmationOTPRepository;
	
	@ApiOperation(value = "send confirmation code to the user phone number if it is a valid one", response = ResponseEntity.class)
	@ApiResponses(value = { 
	    @ApiResponse(code = 200, message = "confirmation code is sent succefully sent to the given phone number"),
	    @ApiResponse(code = 417, message = "the given phone number is not a valid phone number"),
	    @ApiResponse(code = 409, message = "phone number belongs to an existing user"),
	    @ApiResponse(code = 429, message = "confirmation code is already sent to this phone number")
	})
	@PostMapping("/registration")
	public ResponseEntity<Object> registration(@RequestBody UserForm userForm)  {
		String phoneNumber = userForm.getPhoneNumber();
		boolean isPhoneNumberValid = accountService.PhoneNumberValidation(phoneNumber);
		
		if(!isPhoneNumberValid) {
			logger.error("the phone number format is not correct {}", phoneNumber);
			return new ResponseEntity<>(new Error("INVALIDE_PHONE_NUMBER"), HttpStatus.EXPECTATION_FAILED);
			}
		User user = accountService.getUserByPhoneNumber(phoneNumber);
		if( user != null) {
				logger.error("{} this phone number already exists", phoneNumber);
				return new ResponseEntity<>(new Error("USER_ALREADY_EXIST"), HttpStatus.CONFLICT);
		}
		String requestId = accountService.startPhoneNumberVerification(phoneNumber);
		if( requestId.contentEquals("ERROR")) {
			logger.warn("a confirmation code is already sent to this phone number {}", phoneNumber);
			return new ResponseEntity<>(new ResponseMessage(requestId), HttpStatus.TOO_MANY_REQUESTS);
		}
		Map<String, String> responseObjectDetails = new HashMap<String, String>();
		logger.info("verification code is sent to the user");
		responseObjectDetails.put("requestId", requestId);
		return new ResponseEntity<>(responseObjectDetails, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "using 3rd party(nexmo) to verify phone number", response = ResponseEntity.class)
	@ApiResponses(value = { 
	    @ApiResponse(code = 200, message = "phone number is confirmed succesfully"),
	    @ApiResponse(code = 406, message = "wrong confirmation code")
	})
	@PostMapping("/confirmphonenumber/{requestId}")
	public ResponseEntity<Object> verifyPhoneNumber(@PathVariable("requestId") String  requestId, @RequestBody UserForm  userForm) throws NexmoClientException, IOException{
		String phoneNumber = userForm.getPhoneNumber();
		String result = accountService.checkPhoneNumberVerification(requestId, userForm.getCode());
		if(result.equals("Verification Successful")) 
		{
			@SuppressWarnings("unused")
			int createUser = accountService.createUserPhoneNumber(phoneNumber);
			accountService.userTokenCreation(phoneNumber);
	    	User user = accountService.getUserByPhoneNumber(phoneNumber);
			logger.info("user and token were created  with success!!!");
			return new ResponseEntity<>(user, HttpStatus.OK);
		}
		logger.error("a problem encountered during the phone number verification, the error is {}", "result");
		return new ResponseEntity<>(new Error("result"), HttpStatus.NOT_ACCEPTABLE);
	}
	
	
	
	@ApiOperation(value = "sending an OTP to the email address entered by the user, persiste the user", response = ResponseEntity.class) 
	@ApiResponses(value = { 
	    @ApiResponse(code = 200, message = "OTP is sent to the user email and the user is persisted with success"),
	    @ApiResponse(code = 417, message = "user doesn't exist"),
	    @ApiResponse(code = 401, message = "phone number belongs to an existing user")
	})
	@PostMapping("/addemail")
	public ResponseEntity<Object> addEmail(@RequestBody UserForm userForm){
		boolean isEmailValid = accountService.isEmailValide(userForm.getEmail());
		if(!isEmailValid) {
			logger.error("the email is not a valid email");
			return new ResponseEntity<>(new Error("IVALID_MAIL"), HttpStatus.EXPECTATION_FAILED);
		}
		User user = accountService.getUserByPhoneNumber(userForm.getPhoneNumber());
		if(user == null) {
			logger.error("invalid phone number !");
			return new ResponseEntity<>(new Error("IVALID_PHONE_NUMBER"), HttpStatus.UNAUTHORIZED);
		}
		user.setEmail(userForm.getEmail());
		userRepository.save(user);
		accountService.verifyEmail(user);
		
		logger.info("confirmation email is sent to {} with success!!!",userForm.getEmail());
		
		return new ResponseEntity<>(new ResponseMessage("EMAIL_SENT"), HttpStatus.OK);
	}
	
	@ApiOperation(value = "confirme user email, changing the status of the value of isEnabled and isEmailConfirmed to true if the email is confirmed", response = ResponseEntity.class) 
	@ApiResponses(value = { 
	    @ApiResponse(code = 200, message = "email is confirmed with success"),
	    @ApiResponse(code = 409, message = "Incorrect otp"),
	    @ApiResponse(code = 400, message = "privided otp didn't match the one sent to the user"),
	    @ApiResponse(code = 401, message = "invalid user phone number"),
	    @ApiResponse(code = 417, message = "otp is required"),
	})
	@PostMapping("/confirmaccount")
	public ResponseEntity<Object> confirmUserAccount(HttpServletResponse response,@RequestBody UserForm userForm )
	 {	
			String otpConfirmation = userForm.getCode();
	    	
	    	ConfirmationOTP otp = confirmationOTPRepository.findByConfirmationToken(otpConfirmation);
	    	if(otp == null) {
	    		logger.error("sorry, the confirmation otp provided didn't match any of the existing passwords!");
				return  new ResponseEntity<>(new Error("INVALID_OTP"), HttpStatus.CONFLICT);
	    	}
	    		
	    	if(!userForm.getPhoneNumber().equals(otp.getUser().getPhoneNumber())) {
	    		logger.error("the provided phone number doesn't match the one belongs to the confirmation password!");
	    		return  new ResponseEntity<>(new Error("PHONE_NUMBER_DON'T_MATCH_THE_OTP"), HttpStatus.BAD_REQUEST);
	    	}
	    	User isUserExist = accountService.getUserByPhoneNumber(userForm.getPhoneNumber());
	    	if(isUserExist == null) {
	    		logger.error("the provided phone number doesn't belong to any user!");
	    		return  new ResponseEntity<>(new Error("PHONE_NUMBER_NOT_FOUND"), HttpStatus.UNAUTHORIZED);
	    	}
	    		
	    			
	        if(otpConfirmation == null) {
	        	logger.error("confirmation otp is null!");
	        	return  new ResponseEntity<>(new Error("OTP_REQUIRED"), HttpStatus.EXPECTATION_FAILED);
	        }
			
	        User user = userRepository.getByPhoneNumber(otp.getUser().getPhoneNumber());
            user.setEmailConfirmed(true);
            user.setEnabled(true);
            logger.info("Email confirmation of the user is done");
            return new ResponseEntity<>(new ResponseMessage("EMAIL_IS_VERIFIED"),HttpStatus.OK);	
	            
	        
			
			
	    }
	
	
	
	
	
	
	
}
@Data
class UserForm{
	private String phoneNumber;
	private String lastName;
	private String surname;
	private String email;
	private String code;
	private byte[] idCard;
	private byte[] qrCode;
	private boolean isIDChecked;
	private boolean isEnabled;
	private boolean isEmailConfirmed;
	private String role;
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public String getLastName() {
		return lastName;
	}
	
	public String getSurname() {
		return surname;
	}
	public String getEmail() {
		return email;
	}
	public String getCode() {
		return code;
	}
	public byte[] getIdCard() {
		return idCard;
	}
	public byte[] getQrCode() {
		return qrCode;
	}
	public boolean isIDChecked() {
		return isIDChecked;
	}
	public boolean isEnabled() {
		return isEnabled;
	}
	public boolean isEmailConfirmed() {
		return isEmailConfirmed;
	}
	public String getRole() {
		return role;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public void setIdCard(byte[] idCard) {
		this.idCard = idCard;
	}
	public void setQrCode(byte[] qrCode) {
		this.qrCode = qrCode;
	}
	public void setIDChecked(boolean isIDChecked) {
		this.isIDChecked = isIDChecked;
	}
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public void setEmailConfirmed(boolean isEmailConfirmed) {
		this.isEmailConfirmed = isEmailConfirmed;
	}
	public void setRole(String role) {
		this.role = role;
	}
	@Override
	public String toString() {
		return "UserForm [phoneNumber=" + phoneNumber + ", lastName=" + lastName + ", surname=" + surname + ", email="
				+ email + ", code=" + code + ", idCard=" + Arrays.toString(idCard) + ", qrCode="
				+ Arrays.toString(qrCode) + ", isIDChecked=" + isIDChecked + ", isEnabled=" + isEnabled
				+ ", isEmailConfirmed=" + isEmailConfirmed + ", role=" + role + "]";
	}
}
class ResponseMessage{
	private String message;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ResponseMessage(String msg) {
		this.message = msg;
	}
}
class Error{
	private ResponseMessage error;
	public ResponseMessage getError() {
		return error;
	}
	public void setError(ResponseMessage error) {
		this.error = error;
	}
	public Error(String msg) {
		this.error = new ResponseMessage(msg);
	}
	public Error(ResponseMessage errorMessage) {
		this.error = errorMessage;
	}
}
