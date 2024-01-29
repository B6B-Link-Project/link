package com.link.back.controller;

import static com.link.back.entity.MemberStatus.*;
import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.link.back.dto.request.CreateTeamRequestDto;
import com.link.back.dto.request.UpdateTeamRequestDto;
import com.link.back.dto.request.UserSearchConditionDto;
import com.link.back.dto.response.CandidatesResponseDto;
import com.link.back.dto.response.MemberDetailResponseDto;
import com.link.back.dto.response.TeamApplicationResponseDto;
import com.link.back.dto.response.TeamResponseDto;
import com.link.back.entity.Field;
import com.link.back.service.TeamBuildingService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teams")
@Validated
@RequiredArgsConstructor
@CrossOrigin
public class TeamBuildingController {

	private final TeamBuildingService teamBuildingService;

	// 팀 참가 신청
	@PostMapping("/{teamId}/members/apply")
	@ResponseStatus(CREATED)
	public void postTeamParticipateApply(@PathVariable @Positive Long teamId, @Positive Long userId) {
		userId = 11L;
		teamBuildingService.teamParticipate(teamId, userId, APPLIED);
	}

	// 팀 참가 권유
	@PostMapping("/{teamId}/members/{userId}/suggest")
	@ResponseStatus(CREATED)
	public void postSuggestionByTeam(@PathVariable @Positive Long teamId, @PathVariable @Positive Long userId) {
		teamBuildingService.teamParticipate(teamId, userId, SUGGESTED);
	}

	// 팀 참가 신청 거절
	@DeleteMapping("/{teamId}/members/{userId}/apply")
	@ResponseStatus(NO_CONTENT)
	public void deleteTeamParticipateApply(@PathVariable @Positive Long teamId, @PathVariable @Positive Long userId) {
		teamBuildingService.refuseOrCancelTeamParticipate(teamId, userId, APPLIED);
	}

	// 팀 참가 권유 거절
	@DeleteMapping("/{teamId}/members/suggest")
	@ResponseStatus(NO_CONTENT)
	public void deleteSuggestionByTeam(@PathVariable @Positive Long teamId, @Positive Long userId) {
		teamBuildingService.refuseOrCancelTeamParticipate(teamId, 11L, SUGGESTED);
	}

	// 팀 참가 신청 수락
	@PostMapping("/{teamId}/members/{userId}/apply")
	@ResponseStatus(OK)
	public void postTeamParticipate(@PathVariable @Positive Long teamId, @PathVariable @Positive Long userId) {
		teamBuildingService.acceptTeamParticipate(teamId, userId, APPLIED);
	}

	// 팀 참가 권유 수락
	@PostMapping("/{teamId}/members/suggest")
	@ResponseStatus(OK)
	public void postSuggestionByUser(@PathVariable @Positive Long teamId, @Positive Long userId) {
		teamBuildingService.acceptTeamParticipate(teamId, 11L, SUGGESTED);
	}

	// 팀 참가 신청 취소
	@DeleteMapping("{teamId}/members/apply")
	@ResponseStatus(NO_CONTENT)
	public void deleteTeamParticipate(@PathVariable @Positive Long teamId, @Positive Long userId) {
		teamBuildingService.refuseOrCancelTeamParticipate(teamId, 11L, SUGGESTED);
	}

	// 팀 참가 권유 취소
	@DeleteMapping("/{teamId}/members/{userId}/suggest")
	@ResponseStatus(NO_CONTENT)
	public void deleteSuggestionByUser(@PathVariable @Positive Long teamId, @PathVariable @Positive Long userId) {
		teamBuildingService.refuseOrCancelTeamParticipate(teamId, userId, SUGGESTED);
	}

	// 팀 참가 신청받은 목록 조회(팀장)
	@GetMapping("/{teamId}/applied")
	@ResponseStatus(OK)
	public CandidatesResponseDto teamParticipateList(@PathVariable @Positive Long teamId) {
		return teamBuildingService.getSuggestionListOfTeam(teamId, APPLIED);
	}

	// 팀 참가 권유한 목록 조회(팀장)
	@GetMapping("/{teamId}/suggesting")
	@ResponseStatus(OK)
	public CandidatesResponseDto getSuggestionListOfTeam(@PathVariable @Positive Long teamId) { // todo: create dto
		return teamBuildingService.getSuggestionListOfTeam(teamId, SUGGESTED);
	}

	// 팀 참가 신청한 팀 목록 조회
	@GetMapping("/applying")
	@ResponseStatus(OK)
	public TeamApplicationResponseDto getTeamParticipateApplyingList(@Positive Long userId,
		@RequestParam(required = false) @Positive Long teamId) {
		return teamBuildingService.getTeamParticipateList(11L, teamId, APPLIED);
	}

	// 팀 참가 권유받은 팀 목록 조회
	@GetMapping("/suggested")
	@ResponseStatus(OK)
	public TeamApplicationResponseDto getTeamParticipateSuggestionList(@Positive Long userId,
		@RequestParam(required = false) @Positive Long teamId) {
		return teamBuildingService.getTeamParticipateList(8L, teamId, SUGGESTED);
	}

	// 팀원 탈퇴
	@DeleteMapping("/{teamId}/members")
	@ResponseStatus(NO_CONTENT)
	public void leaveTeamMember(@PathVariable @Positive Long teamId, @Positive Long userId) {
		teamBuildingService.leaveTeamMember(teamId, userId);
	}

	// 팀 생성 // 이미지 업로드 처리
	@PostMapping
	@ResponseStatus(OK)
	public void createTeam(@RequestBody @Valid CreateTeamRequestDto createTeamRequestDto, @Positive Long userId) {
		teamBuildingService.createTeam(createTeamRequestDto, userId);
	}

	// 팀 삭제
	@DeleteMapping("/{teamId}")
	@ResponseStatus(NO_CONTENT)
	public void deleteTeam(@PathVariable @Positive Long teamId) {
		teamBuildingService.deleteTeam(teamId);
	}

	// 팀 정보 수정
	@PutMapping("/{teamId}")
	@ResponseStatus(OK)
	public void updateTeam(@RequestBody @Valid UpdateTeamRequestDto updateTeamRequestDto,
		@PathVariable @Positive Long teamId) {
		teamBuildingService.updateTeam(updateTeamRequestDto, teamId);
	}

	// 팀 정보 조회
	@GetMapping("/{teamId}")
	@ResponseStatus(OK)
	public TeamResponseDto getTeam(@PathVariable @Positive Long teamId) {
		return teamBuildingService.findTeam(teamId);
	}

	@GetMapping("/recruit/detail/{userId}")
	@ResponseStatus(OK)
	public MemberDetailResponseDto getMemberDetail(@PathVariable @Positive Long userId) {
		return teamBuildingService.getMemberInfoDetail(userId);
	}

	@GetMapping("/recruit")
	@ResponseStatus(OK)
	public List<MemberDetailResponseDto> findMemberByCond(
		Pageable pageable,
		@RequestParam(required = false) UserSearchConditionDto userSearchConditionDto,
		@RequestParam(required = false) String field) {
		if (field != null && !field.isEmpty()) {
			userSearchConditionDto.setField(Field.valueOf(field));
		}
		return teamBuildingService.findMemberByCond(pageable, userSearchConditionDto);
	}

}
