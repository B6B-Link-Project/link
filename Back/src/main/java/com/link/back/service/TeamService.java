package com.link.back.service;

import static com.link.back.entity.Role.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.link.back.dto.request.TeamSearchConditionDto;
import com.link.back.dto.response.IdResponseDto;
import com.link.back.dto.response.IdsResponseDto;
import com.link.back.dto.response.TeamRecruitResponseDto;
import com.link.back.dto.response.TeamSkillAddDto;
import com.link.back.entity.Team;
import com.link.back.entity.User;
import com.link.back.entity.UserTeam;
import com.link.back.exception.ContentNotFoundException;
import com.link.back.repository.SkillRepository;
import com.link.back.repository.TeamRepository;
import com.link.back.repository.UserRepository;
import com.link.back.repository.UserTeamRepository;
import com.link.back.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {

	@Value("${project.url}")
	private String url;

	@Value("${spring.mail.username}")
	private String fromEmail;

	private final TeamRepository teamRepository;
	private final UserRepository userRepository;
	private final UserTeamRepository userTeamRepository;
	private final SkillRepository skillRepository;
	private final EmailService emailService; // todo: user Async

	private final JwtTokenProvider jwtTokenProvider;

	private final RedisTemplate<String, String> redisTemplate;

	public IdResponseDto getTeamId(String token) {
		long userId = jwtTokenProvider.getUserId(token);

		User user = userRepository.findById(userId)
			.orElseThrow(ContentNotFoundException::new);

		Team team = teamRepository.findActiveTeamByUser(user);

		return new IdResponseDto(team.getTeamId());
	}

	public IdsResponseDto getTeamIds(String token) {
		long userId = jwtTokenProvider.getUserId(token);

		User user = userRepository.findById(userId)
			.orElseThrow(ContentNotFoundException::new);

		List<Team> teams = teamRepository.findBuildingTeamsByUser(user);

		List<Long> ids = teams.stream()
			.map(Team::getTeamId)
			.toList();
		List<String> names = teams.stream()
			.map(Team::getTeamName)
			.toList();

		return new IdsResponseDto(ids, names);
	}

	public void requestToRemoveMember(Long excludedMemberId, String token) {
		long leaderId = jwtTokenProvider.getUserId(token);
		User loginUser = userRepository.findById(leaderId)
			.orElseThrow(ContentNotFoundException::new); // todo: create exception

		Team team = teamRepository.findActiveTeamByUser(loginUser);

		UserTeam leader = userTeamRepository.findUserTeamByTeamAndUser(team, loginUser);

		if (leader == null || leader.getRole() != LEADER) {
			throw new RuntimeException(); // todo: create exception
		}

		User excludedUser = userRepository.findById(excludedMemberId)
			.orElseThrow(RuntimeException::new); // todo: create exception

		UserTeam excludedMember = userTeamRepository.findUserTeamByTeamAndUser(team, excludedUser);

		List<UserTeam> members = userTeamRepository.findUserTeamsByTeam(team);
		members.remove(excludedMember);
		members.remove(leader);

		Map<UserTeam, UUID> uuids = new HashMap<>();
		members.forEach(member -> uuids.put(member, UUID.randomUUID()));

		// save uuid at redis and send email
		HashOperations<String, Object, Object> operations = redisTemplate.opsForHash();

		for (Map.Entry<UserTeam, UUID> entry : uuids.entrySet()) {
			UserTeam member = entry.getKey();
			UUID uuid = entry.getValue();

			operations.put(excludedMemberId.toString(), member.getUserTeamId().toString(), uuid.toString());

			emailService.sendEmail(fromEmail, member.getUser().getEmail(),
				"팀원 " + excludedMember.getUser().getName() + " 삭제 허가 요청",
				getRemoveMemberEmailContent(url, team.getTeamId(), excludedMemberId, member.getUserTeamId(), uuid), false);
		}
	}

	// todo: change target url localhost:8080
	private String getRemoveMemberEmailContent(String url, Long teamId, Long excludedMemberId, Long memberId, UUID uuid) {
		return "<!doctype html>\n"
			+ "<html lang=\"ko\">\n"
			+ "<head>\n"
			+ "    <meta charset=\"UTF-8\">\n"
			+ "    <meta name=\"viewport\"\n"
			+ "          content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\">\n"
			+ "    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n"
			+ "    <title>팀원 삭제 허가 요청</title>\n"
			+ "</head>\n"
			+ "<body>\n"
			+ "<a href=\"http://" + url + "/api/teams/" + teamId + "/members/" + excludedMemberId
			+ "/permission/remove?memberId= " + memberId + "&code=" + uuid + "\">Permit Request</a>"
			+ "</body>\n"
			+ "</html>";
	}

	public void permitToRemoveMember(Long teamId, Long excludedMemberId, Long memberId, String uuid) {
		HashOperations<String, Object, Object> operations = redisTemplate.opsForHash();
		String savedUuid = (String)operations.get(excludedMemberId.toString(), memberId.toString());

		if (savedUuid != null && savedUuid.equals(uuid)) {
			operations.delete(excludedMemberId.toString(), memberId.toString());
		}

		if (operations.size(excludedMemberId.toString()) == 0) {
			Team team = teamRepository.findById(teamId)
				.orElseThrow(RuntimeException::new);// todo: create exception
			User excludedUser = userRepository.findById(excludedMemberId)
				.orElseThrow(RuntimeException::new); // todo: create exception

			removeMember(team, excludedUser);
		}
	}

	private void removeMember(Team team, User user) {
		userTeamRepository.deleteUserTeamByTeamAndUser(team, user);
	}

	public Page<TeamRecruitResponseDto> findTeamByHackathonAndTeamSearchCond(Long hackathonId, TeamSearchConditionDto teamSearchConditionDto, Pageable pageable) {
		Page<Team> teamList = teamRepository.findTeamByHackathonAndTeamSearchCond(hackathonId, teamSearchConditionDto, pageable);
		List<TeamRecruitResponseDto> teamRecruitResponseDtos = teamList.stream().map(TeamRecruitResponseDto::new).toList();
		return new PageImpl<>(teamRecruitResponseDtos, pageable, teamList.getTotalElements());
	}

	public List<TeamRecruitResponseDto> findAllTeam() {
		return teamRepository.findAllTeam().stream().map(TeamRecruitResponseDto::new).toList();
	}

	public List<TeamSkillAddDto> findTeamSKillAdd() {
		return skillRepository.findAll().stream().map(TeamSkillAddDto::new).toList();
	}
}
