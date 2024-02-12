package com.link.back.dto.response;

import com.link.back.entity.Skill;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamSkillAddDto {

	private final Long skillId;
	private final String skillName;
	private final String skillImgUrl;

	public TeamSkillAddDto(Skill skill) {
		this.skillId = skill.getSkillId();
		this.skillName = skill.getSkillName();
		this.skillImgUrl = skill.getSkillImageUrl();
	}

}
