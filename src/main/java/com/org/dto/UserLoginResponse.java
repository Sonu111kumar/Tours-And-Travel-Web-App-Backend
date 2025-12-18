package com.org.dto;

import lombok.Data;

@Data
public class UserLoginResponse extends CommanApiResponse{

	private int id;
	
	private String firstName;

	private String lastName;

	private String picture;

	private int age;

	private String sex;

	private String emailId;

	private String contact;

	private String street;

	private String city;

	private String pincode;
	
	private String role;

	private int hotelId;
	
	private String jwtToken;

	
	
}
