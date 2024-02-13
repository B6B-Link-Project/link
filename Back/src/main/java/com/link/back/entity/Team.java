package com.link.back.entity;

import static com.link.back.config.AppConstant.*;
import static com.link.back.entity.TeamStatus.*;
import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Team {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long teamId;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "hackathon_id", nullable = false)
	private Hackathon hackathon;

	@OneToMany(mappedBy = "team", cascade = REMOVE)
	private List<UserTeam> userTeamList = new ArrayList<>();

	@Column(length = TEAM_NAME_LENGTH, nullable = false)
	private String teamName;

	@Enumerated(STRING)
	@Column(nullable = false)
	private TeamStatus teamStatus;

	@Column(nullable = false)
	private Integer teamMember;

	@Column(length = TEAM_DESC_LENGTH)
	private String teamDesc;

	@OneToMany(mappedBy = "team", cascade = PERSIST)
	List<TeamSkill> teamSkills = new ArrayList<>();

	@Builder
	public Team(String teamName, TeamStatus teamStatus, Integer teamMember, String teamDesc, Hackathon hackathon) {
		this.teamName = teamName;
		this.teamStatus = teamStatus;
		this.teamMember = teamMember;
		this.teamDesc = teamDesc;
		this.hackathon = hackathon;
	}

	@Builder(builderMethodName = "updateBuilder")
	public Team(String teamName, String teamDesc, List<TeamSkill> teamSkills) {
		this.teamName = teamName;
		this.teamDesc = teamDesc;
		this.teamSkills = teamSkills;
	}

	public void makeProject() {
		this.teamStatus = COMPLETE;
	}

	public void joinTeam() {
		this.teamMember++;
	}

	public void removeMember() {
		this.teamMember--;
	}

}
