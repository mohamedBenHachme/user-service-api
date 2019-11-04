package com.sid.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import com.sid.service.AccountService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private AccountService AccountService;
	
	@Override
	public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		com.sid.entities.User appUser = AccountService.getUserByPhoneNumber(phoneNumber);
		if( appUser == null) throw new UsernameNotFoundException("invalid user");
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		if( appUser.getRole().equals("Agent")) {
			authorities.add(new SimpleGrantedAuthority("Agetn"));
		}else {
			authorities.add(new SimpleGrantedAuthority("Customer"));
		}
		return  new User(appUser.getPhoneNumber(), appUser.getPhoneNumber(), authorities);
	}

}
