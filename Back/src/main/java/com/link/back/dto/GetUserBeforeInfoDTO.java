package com.link.back.dto;

import java.time.LocalDate;

import com.link.back.entity.User;
import com.link.back.entity.UserImage;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetUserBeforeInfoDTO {

		private String name;
		private LocalDate birth;
		private String phoneNumber;
		private boolean gender;
		// private List<SkillResponseDto> userSkills;
		private String referenceUrl;
		// private Field field;
		private int career;
		private UserImage userImage;
		private boolean registered;
		private String introduce;

		@Builder
		public GetUserBeforeInfoDTO(User user) {
			this.name = user.getName();
			this.birth = user.getBirth();
			this.phoneNumber = user.getPhoneNumber();
			this.gender = user.isGender();
			// this.userSkills = user.getUserSkills().stream()
			// 	.map(SkillResponseDto::new).collect(toList());
			this.referenceUrl = user.getReferenceUrl();
			// this.field = user.getField();
			this.career = user.getCareer();
			this.userImage = user.getUserImage();
			this.registered = user.isRegistered();
			this.introduce = user.getIntroduce();
		}

}
