package com.link.back.dto.response;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;

import com.link.back.entity.Hackathon;
import com.link.back.entity.Role;
import com.link.back.entity.Team;
import com.link.back.entity.UserTeam;

import lombok.Getter;

@Getter
public class TeamApplicationResponseDto {

	private final Long teamId;
	private final String teamName;
	private final String teamDesc;
	private final HackathonInfoResponseDto hackathonInfoResponseDto;
	private TeamMemberResponseDto leader;
	private final List<TeamMemberResponseDto> members = new ArrayList<>();
	private final List<TeamListSimpleResponseDto> teams;

	public TeamApplicationResponseDto(List<Team> teams, Team team, List<UserTeam> members) {
		this.teamId = team.getTeamId();
		this.teams = teams.stream()
			.map(TeamListSimpleResponseDto::new)
			.collect(toList());
		this.teamName = team.getTeamName();
		this.teamDesc = team.getTeamDesc();
		Hackathon hackathon = team.getHackathon();
		this.hackathonInfoResponseDto =	new HackathonInfoResponseDto(hackathon);
		members.forEach(member -> {
			if (member.getRole() == Role.LEADER) {
				this.leader = new TeamMemberResponseDto(member);
			} else {
				this.members.add(new TeamMemberResponseDto(member));
			}
		});

	}
}
