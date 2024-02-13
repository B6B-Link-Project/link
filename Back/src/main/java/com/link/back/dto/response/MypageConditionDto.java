package com.link.back.dto.response;

import static com.link.back.entity.MemberStatus.*;

import java.util.List;

import com.link.back.entity.MemberStatus;
import com.link.back.entity.TeamStatus;
import com.link.back.entity.UserTeam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MypageConditionDto {

	private boolean applied;
	private boolean suggested;
	private boolean project;
	private boolean team;

	public MypageConditionDto(List<UserTeam> userTeamList, boolean hasProject) {
		for (UserTeam userTeam : userTeamList) {
			MemberStatus status = userTeam.getMemberStatus();
			if(status == JOINED) {
				if(userTeam.getTeam().getTeamStatus() != TeamStatus.COMPLETE) {
					team = true;
				}
			} else if (status == SUGGESTED) {
				suggested = true;
			} else if (status == APPLIED) {
				applied = true;
			}
		}
		// userTeamList.stream()
		// 	.map(UserTeam::getMemberStatus)
		// 	.map(Enum::name)
		// 	.forEach(status -> {
		// 		switch (status) {
		// 			case "JOINED" -> team = true;
		// 			case "SUGGESTED" -> suggested = true;
		// 			case "APPLIED" -> applied = true;
		// 		}
		// 	});
		this.project = hasProject;
	}
}
