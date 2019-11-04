package com.sid.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.verify.CheckResponse;
import com.nexmo.client.verify.VerifyResponse;
import com.nexmo.client.verify.VerifyStatus;
import com.sid.dao.ConfirmationOTPRepository;
import com.sid.dao.UserRepository;
import com.sid.entities.ConfirmationOTP;
import com.sid.entities.User;
import com.sid.security.SecurityParams;
@Service
@Transactional
public class AccountServiceImpl implements AccountService{
	@Autowired
	private UserRepository userRepository;
	@SuppressWarnings("unused")
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ConfirmationOTPRepository confirmationOTPRepository;
    @Autowired
    private JavaMailSender javaMailSender;

    
    public AccountServiceImpl(UserRepository appUserRepository,BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = appUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
     
    @Override
	public void userTokenCreation(String phoneNumber)  {
		User user = getUserByPhoneNumber(phoneNumber);
		if(user == null)
			throw new IllegalArgumentException();
        List<String> roles=new ArrayList<>();
        roles.add(user.getRole());
        String jwt= JWT.create()
                .withSubject(phoneNumber)
                .withArrayClaim("roles",roles.toArray(new String[roles.size()]))
                .withExpiresAt(new Date(System.currentTimeMillis()+SecurityParams.EXPIRATION))
                .sign(Algorithm.HMAC256(SecurityParams.SECRET));
        user.setUserToken(jwt);
        userRepository.save(user);
        return ;
    }
    @Override
    public boolean tokenVerification(String phoneNumber, String token) {
    	String userToken = getUserByPhoneNumber(phoneNumber).getUserToken();
    	return (userToken.equals(token))?true:false;
    }
    
	@Override
	public byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
		    QRCodeWriter qrCodeWriter = new QRCodeWriter();
		    BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
		    
		    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
		    byte[] pngData = pngOutputStream.toByteArray(); 
		    return pngData;
		
	}
	
	@Override
	public int createUserPhoneNumber(String phoneNumber) {
		User user = userRepository.getByPhoneNumber(phoneNumber);
		if( user != null) return 0;
		User appUser = new User();
		appUser.setPhoneNumber(phoneNumber);
		userRepository.save(appUser);
		return 1;
	}

	

	@Override
	public User saveUserInformations(String phoneNumber, String lastName, String surname, String email,
			String role) {
		User user = getUserByPhoneNumber(phoneNumber);
		user.setEmail(email);
		user.setLastName(lastName);
		user.setSurname(surname);
		user.setRole(role);
		try {
			user.setQrCode(getQRCodeImage(phoneNumber+" "+lastName+" "+surname, 350, 350));
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}

	@Override
	public User getUserByPhoneNumber(String phoneNumber) {
		return userRepository.getByPhoneNumber(phoneNumber);
	}

	@Override
	public boolean isAgent(String phoneNumber) {
		String role = userRepository.getUserRole(phoneNumber);
		if( role.equals("Agent"))
				return true;
		return false;
	}

	@Override
	public String startPhoneNumberVerification(String phoneNumber) {
		NexmoClient client = NexmoClient.builder().apiKey(NEXMO_API_KEY).apiSecret(NEXMO_API_SECRET).build();
        VerifyResponse response = client.getVerifyClient().verify(phoneNumber, "NEXMO");

        if (response.getStatus() == VerifyStatus.OK) {
        	System.out.println("Request id "+ response.getRequestId());
            return response.getRequestId();
        }
        System.out.println(response.getErrorText());
         return "ERROR! "+response.getStatus() + " "+response.getErrorText();
        
	}
	
	
	
	@Override
	public String checkPhoneNumberVerification(String requestId, String code) throws NexmoClientException, IOException {
		NexmoClient client = NexmoClient.builder().apiKey(NEXMO_API_KEY).apiSecret(NEXMO_API_SECRET).build();
        CheckResponse response = client.getVerifyClient().check(requestId, code);

        if (response.getStatus() == VerifyStatus.OK) {
            return "Verification Successful";
        } else {
            return "Error "+response.getErrorText();
        }
	}

	
	 @Async
	 public void sendEmail(SimpleMailMessage email) {
	        javaMailSender.send(email);
	  }

	@Override
	public  ModelAndView verifyEmail(User user) {
		ConfirmationOTP confirmationOTP = new ConfirmationOTP(user);
		confirmationOTPRepository.save(confirmationOTP);
		SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(confirmationOTP.getUser().getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("Flossy@2019.ma");
        mailMessage.setText("To confirm your account, enter this code :  " + confirmationOTP.getConfirmationOTP());

        sendEmail(mailMessage);
        ModelAndView modelAndView = new ModelAndView();
        
        modelAndView.addObject("emailId", user.getEmail());

        
		modelAndView.setViewName("successfulRegisteration");
		return modelAndView;
	}
	@Override
	public boolean PhoneNumberValidation(String phoneNumber) {
		if(phoneNumber == null)
			return false;
		String regex = "^(\\+212|0)([ \\-_/]*)(\\d[ \\-_/]*){9}$";
		 
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(phoneNumber);

		return matcher.matches();
		
		
	}
	@Override
	public boolean isEmailValide(String email) {
		if(email == null)
			return false;
		boolean isValid = false;
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            isValid = true;
        } catch (AddressException e) {
            return isValid;
        }
		return isValid;
        
	}

	
	 
	 
}
