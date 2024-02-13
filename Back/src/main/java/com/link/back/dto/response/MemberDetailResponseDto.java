package com.link.back.dto.response;

import static java.util.stream.Collectors.*;

import java.util.List;

import com.link.back.entity.Field;
import com.link.back.entity.User;

import lombok.Getter;

@Getter
public class MemberDetailResponseDto {

	private final Long userId;
	private final String name;
	private final String introduce;
	private final int rating;
	private final Integer career;
	private final String referenceUrl;
	private final Field field;
	private String profileOriginImageName = "";
	private String userImageUrl = "";
	private final List<SkillDto> skillSets;

	public MemberDetailResponseDto(User user) {
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
