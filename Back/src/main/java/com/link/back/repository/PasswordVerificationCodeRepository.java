package com.link.back.repository;

import org.springframework.data.repository.CrudRepository;

import com.link.back.dto.PasswordVerificationCode;

public interface PasswordVerificationCodeRepository extends CrudRepository<PasswordVerificationCode, String>{

}