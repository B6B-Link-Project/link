package com.link.back.service;

import static com.link.back.entity.MemberStatus.*;
import static com.link.back.entity.Role.*;
import static com.link.back.entity.TeamStatus.*;
import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.link.back.dto.request.CreateTeamRequestDto;
import com.link.back.dto.request.UpdateTeamRequestDto;
import com.link.back.dto.request.UserSearchConditionDto;
import com.link.back.dto.response.CandidatesResponseDto;
import com.link.back.dto.response.MemberDetailResponseDto;
import com.link.back.dto.response.MypageConditionDto;
import com.link.back.dto.response.RecruitingTeamResponseDto;
import com.link.back.dto.response.TeamApplicationResponseDto;
import com.link.back.dto.response.TeamResponseDto;
import com.link.back.entity.Hackathon;
import com.link.back.entity.MemberStatus;
import com.link.back.entity.Team;
import com.link.back.entity.TeamSkill;
import com.link.back.entity.TeamStatus;
import com.link.back.entity.User;
import com.link.back.entity.UserTeam;
import com.link.back.repository.HackathonRepository;
import com.link.back.repository.ProjectRepository;
import com.link.back.repository.SkillRepository;
import com.link.back.repository.TeamRepository;
import com.link.back.repository.TeamSkillRepository;
import com.link.back.repository.UserRepository;
import com.link.back.repository.UserTeamRepository;
import com.link.back.security.JwtTokenProvider;

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
	private final JwtTokenProvider jwtTokenProvider;
	private final HackathonRepository hackathonRepository;
	private final SkillRepository skillRepository;

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
		User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
		Team team = teamRepository.findById(teamId).orElseThrow(RuntimeException::new);
		userTeamRepository.deleteUserTeamByTeamAndUser(team, user);
		team.removeMember();
	}

	public void createTeam(CreateTeamRequestDto createTeamRequestDto, Long hackathonId, Long userId) {

		Team team = Team.builder()
			.teamName(createTeamRequestDto.getTeamName())
			.teamStatus(TeamStatus.BUILDING)
			.teamMember(1)
			.teamDesc(createTeamRequestDto.getTeamDesc())
			.hackathon(hackathonRepository.findById(hackathonId).orElseThrow(RuntimeException::new))
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
		List<Long> teamSkills = createTeamRequestDto.getTeamSkills();
		for (Long skill : teamSkills) {
			TeamSkill teamSkill = TeamSkill.builder()
				.team(team)
				.skill(skillRepository.findById(skill).orElseThrow(RuntimeException::new))
				.build();
			teamSkillRepository.save(teamSkill);
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

	public Page<MemberDetailResponseDto> findMemberByCond(Pageable pageable,
		UserSearchConditionDto userSearchConditionDto) {
		Page<User> userPage = userRepository.findBySearchCondition(pageable, userSearchConditionDto);
		List<MemberDetailResponseDto> memberDetailResponseDtos = userPage.stream()
			.map(MemberDetailResponseDto::new)
			.toList();

		return new PageImpl<>(memberDetailResponseDtos, pageable, userPage.getTotalElements());
	}

	public boolean isLeader(String token) {
		Long userId = jwtTokenProvider.getUserId(token);
		User user = userRepository.findById(userId)
			.orElseThrow(RuntimeException::new);

		UserTeam leader = userTeamRepository.findUserTeamIfLeader(user);
		return leader != null;
	}

	public RecruitingTeamResponseDto findRecruitingTeam(Long userId) {
		Team team = userTeamRepository.findByIdAndStatus(userId)
			.orElseThrow(RuntimeException::new)
			.getTeam();
		Long teamId = team.getTeamId();
		return new RecruitingTeamResponseDto(userTeamRepository.findUserTeamsByTeamId(teamId));
	}

	public MypageConditionDto findMypageCondition(Long userId) {
		List<UserTeam> userTeamList = userTeamRepository.findUserTeamsByUserId(userId);
		boolean hasProject = userTeamList.stream()
			.filter(userTeam -> userTeam.getMemberStatus() == MemberStatus.JOINED)
			.map(UserTeam::getTeam)
			.map(Team::getTeamId)
			.map(projectRepository::findByTeamId)
			.anyMatch(Objects::nonNull);
		return new MypageConditionDto(userTeamList, hasProject);
	}

	public Boolean findButtonIsValid(Long userId, Long id) {
		Long teamId = 0L;
		for (UserTeam userTeam : userTeamRepository.findUserTeamsByUserId(id)) {
			if (userTeam.getMemberStatus() == JOINED) {
				teamId = userTeam.getTeam().getTeamId();
				break;
			}
		}
		if (teamId == 0L) {
			return false;
		}

		Team team = teamRepository.findById(teamId).orElseThrow(RuntimeException::new);
		User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
		Hackathon hackathon = team.getHackathon();

		int nowPoint = 0;
		for (UserTeam userTeam : team.getUserTeamList()) {
			if (userTeam.getMemberStatus() == JOINED) {
				nowPoint += userTeam.getUser().getRating() / 50 + 1;
			}
		}

		if (team.getTeamStatus() == COMPLETE || team.getTeamMember().equals(hackathon.getMaxTeamMember()) ||
			user.getRating() / 50 + 1 > hackathon.getMaxPoint() - nowPoint) {
			return false;
		}

		// for (UserTeam userTeam : userTeamRepository.findUserTeamsByUserId(userId)) {
		// 	if (userTeam.getUser().getJoinStatus() || userTeam.getTeam().getTeamId().equals(team.getTeamId())) {
		// 		return false;
		// 	}
		// }
		return true;
	}

	public Boolean findApplyButtonIsVaild(Long teamId, Long userId) {
		User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
		if(user.getJoinStatus()) {
			return false;
		}
		Team team = teamRepository.findById(teamId).orElseThrow(RuntimeException::new);
		List<UserTeam> membersByTeamId = userTeamRepository.findMembersByTeamId(teamId);
		int nowPoint = 0;
		for (UserTeam userTeam : membersByTeamId) {
			nowPoint += userTeam.getUser().getRating() / 50 + 1;
		}
		return team.getHackathon().getMaxPoint() >= nowPoint + user.getRating() / 50 + 1;
	}
}
