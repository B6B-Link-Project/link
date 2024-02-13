package com.link.back.dto.response;

import static java.util.stream.Collectors.*;

import java.util.List;

import com.link.back.entity.Field;
import com.link.back.entity.User;
import com.link.back.entity.UserTeam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecruitTeamMemberResponseDto {
	private final Long userId;
	private final String name;
	private final Integer rating;
	private final String introduce;
	private final Integer career;
	private final String referenceUrl;
	private final Field field;
	private String profileOriginImageName = "";
	private String userImageUrl ="";
	private final List<SkillDto> skillSets;

	public RecruitTeamMemberResponseDto(UserTeam userTeam) {
		User user = userTeam.getUser();
		this.userId = user.getUserId();
		this.name = user.getName();
		this.introduce = user.getIntroduce();
		this.rating = user.getRating();
		this.career = user.getCareer();
		this.referenceUrl = user.getReferenceUrl();
		this.field = user.getField();
		if(user.getUserImage() != null) {
			this.profileOriginImageName = user.getUserImage().getUserOriginImageName();
			this.userImageUrl = user.getUserImage().getUserImageUrl();
		}
		this.skillSets = user.getUserSkills().stream()
			.map(SkillDto::new).collect(toList());
	}

}
