package com.sid;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sid.service.AccountServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceApplicationTests {
	@Autowired
	private AccountServiceImpl accountServiceImpl;
	
	@Test
	public void isEmailValid() {
		String invalidEmail = "aaa@a..com";
		boolean verifyEmail = accountServiceImpl.isEmailValide(invalidEmail);
		assertThat(verifyEmail, is(false));
		
		String validEmail = "mohamed@gmail.com";
		verifyEmail=accountServiceImpl.isEmailValide(validEmail);
		assertThat(verifyEmail, is(true));
		
		
		String nullEmail= null;
		verifyEmail = accountServiceImpl.PhoneNumberValidation(nullEmail);
		assertThat(verifyEmail, is(false));
	}
	
	@Test
	public void phoneNumberValidation() {
		
		String phoneNumberWithoutContryCode = "0612456879";
		boolean verifyPhoneNumber = accountServiceImpl.PhoneNumberValidation(phoneNumberWithoutContryCode);
		assertThat(verifyPhoneNumber, is(true));
		
		String phoneNumberWithContryCode = "+212630586088";
		verifyPhoneNumber = accountServiceImpl.PhoneNumberValidation(phoneNumberWithContryCode);
		assertThat(verifyPhoneNumber, is(true));
		
		String invalidPhoneNumber = "14725";
		verifyPhoneNumber = accountServiceImpl.PhoneNumberValidation(invalidPhoneNumber);
		assertThat(verifyPhoneNumber, is(false));
		
		String nullPhoneNumber = null;
		verifyPhoneNumber = accountServiceImpl.PhoneNumberValidation(nullPhoneNumber);
		assertThat(verifyPhoneNumber, is(false));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void assingTokenToUser() {
		String unregistredPhoneNumber = "111";
		accountServiceImpl.userTokenCreation(unregistredPhoneNumber);
		String registredPhoneNumber = "+212630546";
		accountServiceImpl.userTokenCreation(registredPhoneNumber);
		
	}

}
