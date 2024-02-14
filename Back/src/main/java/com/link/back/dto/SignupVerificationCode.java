package com.link.back.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

@Getter
@RedisHash(value = "SignupVerificationCode", timeToLive = 3 * 60)
public class SignupVerificationCode {
	@Id
	private String verificationCode;
	private final UserSignUpDto userSignUpDto;

	public SignupVerificationCode(String verificationCode, UserSignUpDto userSignUpDto) {

		this.verificationCode = verificationCode;
		this.userSignUpDto = userSignUpDto;

	}
}

