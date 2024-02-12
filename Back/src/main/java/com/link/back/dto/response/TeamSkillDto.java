package com.link.back.dto.response;

import com.link.back.entity.SkillType;
import com.link.back.entity.TeamSkill;

import lombok.Getter;

@Getter
public class TeamSkillDto {

	private final String skillName;
	private final String skillImageUrl;
	private final SkillType skillType;

	public TeamSkillDto(TeamSkill teamSkill) {
		this.skillName = teamSkill.getSkill().getSkillName();
		this.skillImageUrl = teamSkill.getSkill().getSkillImageUrl();
		this.skillType = teamSkill.getSkill().getSkillType();
	}

}
