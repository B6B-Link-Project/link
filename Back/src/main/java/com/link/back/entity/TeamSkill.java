package com.link.back.entity;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Entity;
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
public class TeamSkill {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long teamSkillId;

	@ManyToOne
	@JoinColumn(name = "team_id")
	private Team team;

	@ManyToOne
	@JoinColumn(name = "skill_id")
	private Skill skill;

	@Builder
	public TeamSkill(Team team, Skill skill) {
		this.team = team;
		this.skill = skill;
	}

}
