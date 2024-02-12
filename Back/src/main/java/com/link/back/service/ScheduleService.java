package com.link.back.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.link.back.dto.request.ScheduleRequest;
import com.link.back.dto.response.ScheduleResponse;
import com.link.back.entity.Schedule;
import com.link.back.entity.User;
import com.link.back.exception.ContentNotFoundException;
import com.link.back.repository.ScheduleRepository;
import com.link.back.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
	private final ScheduleRepository scheduleRepository;
	private final UserRepository userRepository;

	public ScheduleResponse getScheduleByUserId(Long userId) {
		User user = findUser(userId);
		List<Schedule> schedules = scheduleRepository.findByUser(user);
		return ScheduleResponse.builder()
				.userId(userId)
				.times(schedules.stream().map(
					Schedule::getAvailableTime
				).collect(Collectors.toList()))
				.build();
	}

	@Transactional
	public ScheduleResponse updateMySchedule(Long userId, ScheduleRequest scheduleRequest) {

		User user = findUser(userId);
		scheduleRepository.deleteByUser(user);

		List<Schedule> schedules =
			scheduleRequest
			.times()
			.stream()
			.map(
				time -> Schedule.builder()
					.user(user)
					.availableTime(time)
					.build()
			)
			.toList();

		scheduleRepository.saveAll(schedules);

		return ScheduleResponse.builder()
			.userId(userId)
			.times(scheduleRequest.times())
			.build();
	}

	private User findUser(Long userId) {
		return userRepository.findById(userId).orElseThrow(ContentNotFoundException::new);
	}

}
