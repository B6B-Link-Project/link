package com.link.back.repository;

import org.springframework.data.repository.CrudRepository;

import com.link.back.dto.SignupVerificationCode;

public interface SignupVerificationCodeRepository extends CrudRepository<SignupVerificationCode, String> { }
