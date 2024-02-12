package com.link.back.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.link.back.dto.request.ScheduleRequest;
import com.link.back.dto.response.ScheduleResponse;
import com.link.back.security.JwtTokenProvider;
import com.link.back.service.ScheduleService;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class ScheduleController {
	private final ScheduleService scheduleService;
	private final JwtTokenProvider jwtTokenProvider;

	@GetMapping
	public ResponseEntity<ScheduleResponse> getMySchedule(@RequestHeader(value = "Authorization", required = true) String token) {
		Long myUserId = this.getUserIdFromToken(token);
		ScheduleResponse scheduleResponse = scheduleService.getScheduleByUserId(myUserId);
		return new ResponseEntity<>(scheduleResponse, HttpStatus.OK);
	}

	@GetMapping("/{user_id}")
	public ResponseEntity<ScheduleResponse> getUserSchedule(@PathVariable("user_id") Long userId) {
		ScheduleResponse scheduleResponse = scheduleService.getScheduleByUserId(userId);
		return new ResponseEntity<>(scheduleResponse, HttpStatus.OK);
	}

	@PutMapping
	public ScheduleResponse updateMySchedule(
		@RequestHeader(value = "Authorization", required = true) String token,
		@RequestBody @NotNull ScheduleRequest scheduleRequest) {
		Long myUserId = this.getUserIdFromToken(token);
		return scheduleService.updateMySchedule(myUserId, scheduleRequest);
	}

	private Long getUserIdFromToken(String token) {
		if (token == null) return null;
		return jwtTokenProvider.getUserId(token);
	}

}
