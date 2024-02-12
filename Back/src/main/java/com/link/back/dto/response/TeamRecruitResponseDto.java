package com.link.back.dto.response;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.util.List;

import com.link.back.entity.Hackathon;
import com.link.back.entity.MemberStatus;
import com.link.back.entity.Team;
import com.link.back.entity.UserTeam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRecruitResponseDto {

	private final Long teamId;
	private final String hackathonName;
	private final int teamMaxPoint;
	private final int teamMaxMember;
	private final LocalDate teamDeadlineDate;
	private final String teamName;
	private final Integer teamMember;
	private final String teamDesc;
	private final List<TeamSkillDto> teamSkills;
	private final List<TeamMemberResponseDto> members;

	public TeamRecruitResponseDto(Team team) {
		Hackathon hackathon = team.getHackathon();
		this.teamId = team.getTeamId();
		this.hackathonName = hackathon.getHackathonName();
		this.teamMaxPoint = hackathon.getMaxPoint();
		this.teamMaxMember = hackathon.getMaxTeamMember();
		this.teamDeadlineDate = hackathon.getTeamDeadlineDate();
		this.teamName = team.getTeamName();
		this.teamMember = team.getTeamMember();
		this.teamDesc = team.getTeamDesc();
		this.teamSkills = team.getTeamSkills().stream()
			.map(TeamSkillDto::new).collect(toList());
		this.members = team.getUserTeamList().stream()
			.filter(userTeam -> userTeam.getMemberStatus() == MemberStatus.JOINED)
			.map(TeamMemberResponseDto::new).collect(toList());
	}

}
