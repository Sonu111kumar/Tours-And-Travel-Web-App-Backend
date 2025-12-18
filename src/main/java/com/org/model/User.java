package com.org.model;
import com.org.dto.UserDto;
import com.org.dto.UserLoginResponse;
import org.springframework.beans.BeanUtils;
import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_user")
@Data
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String firstName;

	private String lastName;

	private int age;

	private String picture;

	private String sex;

	private String emailId;

	private String contact;

	private String street;

	private String city;

	private String pincode;

	private String password;

	private String role;

	private int hotelId;

	private String authProvider;
	private String providerId;
	private String accountStatus;
	private boolean emailVerified;
	private boolean contactVerified;
	private LocalDate dateOfBirth;
	private String addressLine1;
	private String addressLine2;
	private int failedLoginCount;
	private String refreshToken;
	private LocalDateTime lastLogin;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;




	public static UserLoginResponse toUserLoginResponse(User user) {
		UserLoginResponse userLoginResponse = new UserLoginResponse();
		BeanUtils.copyProperties(user, userLoginResponse, "password");
		return userLoginResponse;
	}

	public static UserDto toUserDto(User user) {
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(user, userDto, "password");
		return userDto;
	}

}
