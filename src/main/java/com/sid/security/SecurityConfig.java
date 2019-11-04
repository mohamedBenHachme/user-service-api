package com.sid.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	
	private static final String[] WHITELIST = {
											            "/v2/api-docs",
											            "/swagger-resources",
											            "/swagger-resources/**",
											            "/configuration/ui",
											            "/configuration/security",
											            "/swagger-ui.html",
											            "/webjars/**",
											            "/userservice/confirmaccount",
											            "/userservice/registration",
											            "/userservice/addemail",
											            "/userservice/confirmphonenumber/*"
											    };
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		
		http.csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.authorizeRequests()
		.antMatchers(WHITELIST)
		.permitAll()
		.anyRequest().authenticated()
		.and()
		.addFilterBefore(new UserTokenAuthorizationFiler(),UsernamePasswordAuthenticationFilter.class);
	}

}
