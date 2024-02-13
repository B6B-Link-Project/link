package com.link.back.entity;

import static com.link.back.entity.MemberStatus.*;
import static com.link.back.entity.Role.*;
import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class UserTeam {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long userTeamId;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@JsonIgnoreProperties
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	@Enumerated(STRING)
	private Role role;

	@Enumerated(STRING)
	private MemberStatus memberStatus;

	@Builder
	public UserTeam(Long userTeamId, User user, Team team, Role role, MemberStatus memberStatus) {
		this.userTeamId = userTeamId;
		this.user = user;
		this.team = team;
		this.role = role;
		this.memberStatus = memberStatus;
	}

	@Builder
	public UserTeam(User user, Team team, Role role, MemberStatus memberStatus) {
		this.user = user;
		this.team = team;
		this.role = role;
		this.memberStatus = memberStatus;
	}

	public void joinTeam() {
		role = MEMBER;
		memberStatus = JOINED;
		team.joinTeam();
	}

}
