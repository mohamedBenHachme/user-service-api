package com.sid;



import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.sid.dao.ConfirmationOTPRepository;
import com.sid.dao.UserRepository;
import com.sid.service.AccountServiceImpl;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@SpringBootApplication
public class UserServiceApplication {
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
	return new BCryptPasswordEncoder();
	}
	@Bean
    public Docket swagger() {
        return new Docket(DocumentationType.SWAGGER_2)
        		 .select()
                 .apis(RequestHandlerSelectors
                         .basePackage("com.sid.web"))
                 .paths(PathSelectors.regex("/userservice.*"))
                 .build()
                 .apiInfo(apiInfo());
    }
	private ApiInfo apiInfo() {
        return new ApiInfo(
                "UserService REST API",
                "custom description of API.",
                "API TOS",
                "Terms of service",
                new Contact("Mohamed Ben Hachme", "www.example.com", "benhachmemohamed@gmail.com"),
                "License of API", "API license URL", Collections.emptyList());
    }
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	
	

}
