package com.link.back.dto;

import lombok.Getter;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "PasswordVerificationCode", timeToLive = 3 * 60)
public class PasswordVerificationCode {

	@Id
	private String verificationCode;
	private final String email;

	public PasswordVerificationCode(String verificationCode, String email) {

		this.verificationCode = verificationCode;
		this.email = email;

	}
}
