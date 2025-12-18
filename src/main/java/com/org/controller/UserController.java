package com.org.controller;


import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.org.dto.*;
import com.org.model.User;
import com.org.utility.Constants.ResponseCode;
import com.org.utility.Constants.Sex;
import com.org.utility.Constants.UserRole;

import com.org.config.JwtService;
import com.org.service.CustomUserDetailsService;
import com.org.service.UserService;
import com.org.utility.Constants;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("api/user/")
public class UserController {

	Logger LOG = LoggerFactory.getLogger(UserController.class);


	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

//	 @Autowired private JwtUtil jwtUtil;

	@Autowired private JwtService jwtService;

	@PostMapping("/Googlelogin")
	public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) throws Exception {
		 UserLoginResponse userLoginResponse = new UserLoginResponse();

		 String jwtToken = null;

		String idTokenString = body.get("token");

		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
				.Builder(new NetHttpTransport(), new GsonFactory())
				.setAudience(Collections.singletonList("417959745800-h2k4lsj8te9ne8oa3t4h79ai58qpp4t3.apps.googleusercontent.com"))
				.build();

		GoogleIdToken idToken = verifier.verify(idTokenString);

		if (idToken != null) {
			GoogleIdToken.Payload payload = idToken.getPayload();
			String email = payload.getEmail();
//			String name = (String) payload.get("name");
			String password = null;
			if (userService.getUserByEmailId(email) == null){
				userLoginResponse.setResponseCode(ResponseCode.FAILED.value());
				userLoginResponse.setResponseMessage("Failed to login as "+ email);
				return new ResponseEntity<>(userLoginResponse,HttpStatus.BAD_REQUEST);
			}
//			UserDetails userDetails  = customUserDetailsService.loadUserByUsername(email);
			jwtToken = jwtService.generateToken(email);
			if(jwtToken != null){
				User user = userService.getUserByEmailId(email);
				userLoginResponse = User.toUserLoginResponse(user);
				userLoginResponse.setResponseCode(ResponseCode.SUCCESS.value());
				userLoginResponse.setResponseMessage(email + "logged in successfully");
				userLoginResponse.setJwtToken(jwtToken);
				return new ResponseEntity<>(userLoginResponse,HttpStatus.OK);
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google token");
	}




	@PostMapping("/Googleregister")
	public ResponseEntity<?> googleRegister(@RequestBody Map<String , String> body) throws Exception {
		String idTokenString   = body.get("token");

		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
				.Builder(new NetHttpTransport(), new GsonFactory())
				.setAudience(Collections.singletonList("417959745800-h2k4lsj8te9ne8oa3t4h79ai58qpp4t3.apps.googleusercontent.com"))
				.build();

		GoogleIdToken idToken = verifier.verify(idTokenString);
		if(idToken != null){
			GoogleIdToken.Payload payload = idToken.getPayload();
			String email = payload.getEmail();
			String name = (String) payload.get("name");
			String picture = (String) payload.get("picture");
			String providerId = (String) payload.get("sub");

			if( userService.getUserByEmailId(email) == null){
				User user  = new User();
				user.setEmailId(email);
				user.setFirstName(name);
				user.setPicture(picture);
				user.setAuthProvider("Google");
				user.setProviderId(providerId);
				User user1 = userService.registerUser(user);
				if(user1 != null){
					return new ResponseEntity<>(user1,HttpStatus.CREATED);
				}
				else{
					return new ResponseEntity<>("Register failed",HttpStatus.NOT_ACCEPTABLE);
				}
			}
			else{
				return new ResponseEntity<>("This email already exist!",HttpStatus.NOT_ACCEPTABLE);
			}
		}

		return new ResponseEntity<>("Google Token invalid",HttpStatus.NOT_ACCEPTABLE);

	}

	@GetMapping("roles")
	@Operation(summary = "Api to get all user roles")
	public ResponseEntity<?> getAllUsers() {

		UserRoleResponse response = new UserRoleResponse(); List<String> roles = new
				ArrayList<>();

		for(Constants.UserRole role : Constants.UserRole.values() ) { roles.add(role.value()); }

		if(roles.isEmpty()) { response.setResponseCode(Constants.ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to Fetch User Roles"); return new
					ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR); }

		else { response.setRoles(roles);
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("User Roles Fetched success"); return new
					ResponseEntity(response, HttpStatus.OK); }

	}

	@GetMapping("gender")

	@Operation(summary = "Api to get all user gender") public ResponseEntity<?>
	getAllUserGender() {

		UserRoleResponse response = new UserRoleResponse(); List<String> genders =
				new ArrayList<>();

		for(Sex gender : Sex.values() ) { genders.add(gender.value()); }

		if(genders.isEmpty()) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to Fetch User Genders"); return new
					ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR); }

		else { response.setGenders(genders);
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("User Genders Fetched success"); return new
					ResponseEntity(response, HttpStatus.OK); }

	}

	@PostMapping("register")
	@Operation(summary = "Api to register any User")
	public ResponseEntity<?> register(@RequestBody User user) {
		LOG.info("Recieved request for User  register");

		CommanApiResponse response = new CommanApiResponse();
        user.setAuthProvider("Local");
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User registerUser = userService.registerUser(user);

		if (registerUser != null) {
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage(user.getRole() + " User Registered Successfully");
			return new ResponseEntity(response, HttpStatus.OK); }

		else { response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to Register " + user.getRole() + " User");
			return new ResponseEntity(response,HttpStatus.INTERNAL_SERVER_ERROR); }
	}


	@PostMapping("login")
	@Operation(summary = "Api to login any User")
	public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {

		LOG.info("Recieved request for User Login");
		String jwtToken = null;
		UserLoginResponse useLoginResponse = new UserLoginResponse();
		User user = null;
		try {
			authenticationManager.authenticate( new
					UsernamePasswordAuthenticationToken(userLoginRequest.getEmailId(),
					userLoginRequest.getPassword()));
		}
		catch (Exception ex) {
			LOG.error("Autthentication Failed!!!");
			useLoginResponse.setResponseCode(ResponseCode.FAILED.value());
			useLoginResponse.setResponseMessage("Failed to Login as " + userLoginRequest.getEmailId());
			return new ResponseEntity(useLoginResponse, HttpStatus.BAD_REQUEST); }

		UserDetails userDetails = customUserDetailsService.loadUserByUsername(userLoginRequest.getEmailId());

		for (GrantedAuthority grantedAuthory : userDetails.getAuthorities()) {
			jwtToken = jwtService.generateToken(userDetails.getUsername());
		}


		if (jwtToken != null) {
			user = userService.getUserByEmailId(userLoginRequest.getEmailId());
            userService.updateLoginTime(user);
			useLoginResponse = User.toUserLoginResponse(user);
			useLoginResponse.setResponseCode(ResponseCode.SUCCESS.value());
			useLoginResponse.setResponseMessage(user.getFirstName() + " logged in Successful");
			useLoginResponse.setJwtToken(jwtToken);
			return new ResponseEntity(useLoginResponse, HttpStatus.OK);
		   }

		else {
			useLoginResponse.setResponseCode(ResponseCode.FAILED.value());
			useLoginResponse.setResponseMessage("Failed to Login as " + userLoginRequest.getEmailId());
			return new ResponseEntity(useLoginResponse, HttpStatus.BAD_REQUEST); }
	       }

	@GetMapping("hotel")
	@Operation(summary = "Api to fetch all the Users whose Role is Hotel and not Admin of other Hotel")
	public ResponseEntity<?> fetchAllHotelUsers() {

		UsersResponseDto response = new UsersResponseDto();

		List<User> users =
				userService.getUsersByRoleAndHotelId(UserRole.HOTEL.value(), 0);

		if(users == null || users.isEmpty()) {
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("No Users with Role Hotel found"); }
		    response.setUsers(users);
		    response.setResponseCode(ResponseCode.SUCCESS.value());
	     	response.setResponseMessage("Hotel Users Fetched Successfully");

		return new ResponseEntity(response, HttpStatus.OK);
	}

	@GetMapping("id")
	@Operation(summary = "Api to fetch the User using user Id") public
	ResponseEntity<?> fetchUser(@RequestParam("emailId") String emailId) {

		UsersResponseDto response = new UsersResponseDto();
		User user = userService.getUserByEmailId(emailId);
		if(user == null) { response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("No User with this EmailId present"); }
		    response.setUser(user);
		    response.setResponseCode(ResponseCode.SUCCESS.value());
		    response.setResponseMessage("User Fetched Successfully");
		  return new ResponseEntity(response, HttpStatus.OK);
	}

}