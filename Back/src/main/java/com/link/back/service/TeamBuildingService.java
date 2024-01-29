package com.link.back.service;

import static com.link.back.entity.MemberStatus.*;
import static com.link.back.entity.Role.*;
import static com.link.back.entity.TeamStatus.*;
import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.link.back.dto.request.CreateTeamRequestDto;
import com.link.back.dto.request.UpdateTeamRequestDto;
import com.link.back.dto.request.UserSearchConditionDto;
import com.link.back.dto.response.CandidatesResponseDto;
import com.link.back.dto.response.MemberDetailResponseDto;
import com.link.back.dto.response.TeamApplicationResponseDto;
import com.link.back.dto.response.TeamResponseDto;
import com.link.back.entity.MemberStatus;
import com.link.back.entity.Team;
import com.link.back.entity.TeamStatus;
import com.link.back.entity.User;
import com.link.back.entity.UserTeam;
import com.link.back.repository.ProjectRepository;
import com.link.back.repository.TeamRepository;
import com.link.back.repository.TeamSkillRepository;
import com.link.back.repository.UserRepository;
import com.link.back.repository.UserTeamRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamBuildingService {

	private final TeamRepository teamRepository;
	private final UserRepository userRepository;
	private final UserTeamRepository userTeamRepository;
	private final ProjectRepository projectRepository;
	private final TeamSkillRepository teamSkillRepository;
	public void teamParticipate(Long teamId, Long userId, MemberStatus status) {
		Team team = teamRepository.findById(teamId).orElseThrow(RuntimeException::new);
		User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
		UserTeam userTeam = UserTeam.builder()
			.user(user)
			.team(team)
			.role(CANDIDATE)
			.memberStatus(status)
			.build();
		userTeamRepository.save(userTeam);
	}

	public void refuseOrCancelTeamParticipate(Long teamId, Long userId, MemberStatus status) {
		Team team = teamRepository.findById(teamId)
			.orElseThrow(RuntimeException::new);// todo: create exception

		User user = userRepository.findById(userId)
			.orElseThrow(RuntimeException::new); // todo: create exception

		userTeamRepository.deleteUserTeamByTeamAndUserAndMemberStatus(team, user, status);

		// todo: notification!!!
	}

	public void acceptTeamParticipate(Long teamId, Long userId, MemberStatus status) {
		Team team = teamRepository.findById(teamId)
			.orElseThrow(RuntimeException::new);// todo: create exception

		User user = userRepository.findById(userId)
			.orElseThrow(RuntimeException::new); // todo: create exception

		UserTeam userTeam = userTeamRepository.findUserTeamByTeamAndUserAndMemberStatus(team, user, status);
		userTeam.joinTeam();

		// todo: notification!!!
	}

	@Transactional(readOnly = true)
	public CandidatesResponseDto getSuggestionListOfTeam(Long teamId, MemberStatus status) {
		// Team team = teamRepository.findById(teamId)
		// 	.orElseThrow(RuntimeException::new); // todo: create exception

		List<UserTeam> candidates = userTeamRepository.findCandidates(teamId, status);

		return new CandidatesResponseDto(candidates);
	}

	public TeamApplicationResponseDto getTeamParticipateList(Long userId, Long teamId, MemberStatus status) {
		User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
		// 팀 리스트(teamId, teamName)
		List<UserTeam> userTeamList = userTeamRepository.findUserTeamsByUserAndMemberStatus(user, status);
		List<Team> teams = userTeamList.stream().map(UserTeam::getTeam).collect(toList());
		if (teamId == null)
			teamId = teams.get(0).getTeamId();
		// 팀 정보(teamName, teamDesc), 해커톤 정보
		Team team = teamRepository.findById(teamId).orElseThrow(RuntimeException::new);
		// UserTeam userTeam = userTeamRepository.findUserTeamByTeamIdWithTeamAndHackathon(teamId);
		// 팀 멤버 정보(List<TeamMemberResponseDto>
		List<UserTeam> members = userTeamRepository.findMembersByTeamId(teamId);
		return new TeamApplicationResponseDto(teams, team, members);
	}

	public void leaveTeamMember(Long teamId, Long userId) {
		userTeamRepository.deleteUserTeamByTeamIdAndUserId(teamId, userId);
	}

	public void createTeam(CreateTeamRequestDto createTeamRequestDto, Long userId) {
		Team team = Team.builder()
			.teamName(createTeamRequestDto.getTeamName())
			.teamStatus(TeamStatus.BUILDING)
			.teamMember(0)
			.teamDesc(createTeamRequestDto.getTeamDesc())
			.build();
		teamRepository.save(team);
		User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
		UserTeam userTeam = UserTeam.builder()
			.user(user)
			.team(team)
			.role(LEADER)
			.memberStatus(JOINED)
			.build();
		userTeamRepository.save(userTeam);
		Long teamId = team.getTeamId();
		List<Long> teamSkills = createTeamRequestDto.getTeamSkills();
		for (Long teamSkill : teamSkills) {
			teamSkillRepository.saveById(teamSkill, teamId);
		}
	}

	public void deleteTeam(Long teamId) {
		// 팀 조회
		Team team = teamRepository.findById(teamId)
			.orElseThrow(RuntimeException::new);
		// 팀 삭제 userTeam cascade remove로 자동 삭제
		teamRepository.delete(team);
		if (team.getTeamStatus().equals(COMPLETE)) { // 팀 빌딩 완료 상태일 때
			// User의 프로젝트 참가 여부 false로 변경
			userTeamRepository.findUserTeamsByTeam(team)
				.forEach(userTeam -> userTeam.getUser().changeJoinState());
			// 프로젝트 삭제
			projectRepository.deleteByTeamId(teamId);
		}
	}

	public void updateTeam(@Valid UpdateTeamRequestDto updateTeamRequestDto, Long teamId) {
		teamRepository.findById(teamId)
			.orElseThrow(RuntimeException::new);

		Team.updateBuilder()
			.teamName(updateTeamRequestDto.getTeamName())
			.teamDesc(updateTeamRequestDto.getTeamDesc())
			.teamSkills(updateTeamRequestDto.getTeamSkillSets())
			.build();
	}

	public TeamResponseDto findTeam(Long teamId) {
		Team team = teamRepository.findById(teamId).orElseThrow(RuntimeException::new);
		List<UserTeam> members = userTeamRepository.findUserTeamByTeamAndMemberStatus(team, JOINED);
		return new TeamResponseDto(team, members);
	}

	public MemberDetailResponseDto getMemberInfoDetail(Long userId) {

		User user = userRepository.findUserInfoById(userId);
		return new MemberDetailResponseDto(user);
	}

	public List<MemberDetailResponseDto> findMemberByCond(Pageable pageable, UserSearchConditionDto userSearchConditionDto) {
		Page<User> userPage = userRepository.findBySearchCondition(pageable, userSearchConditionDto);
		return userPage.stream().map(MemberDetailResponseDto::new).collect(toList());
	}
}