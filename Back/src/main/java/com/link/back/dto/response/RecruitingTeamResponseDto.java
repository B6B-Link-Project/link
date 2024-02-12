package com.link.back.dto.response;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;

import com.link.back.entity.Team;
import com.link.back.entity.UserTeam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecruitingTeamResponseDto {

	private final Long teamId;
	private final String teamName;
	private final String teamDesc;
	private final Map<String, List<RecruitTeamMemberResponseDto>> members;

	public RecruitingTeamResponseDto(List<UserTeam> userTeamList) {
		Team team = userTeamList.get(0).getTeam();
		this.teamId = team.getTeamId();
		this.teamName = team.getTeamName();
		this.teamDesc = team.getTeamDesc();
		this.members = userTeamList.stream()
			.collect(groupingBy(
				userTeam -> userTeam.getMemberStatus().name(),
				HashMap::new,
				mapping(RecruitTeamMemberResponseDto::new, toList())
			));
	}

}
