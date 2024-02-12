package com.link.back.dto.response;

import java.util.HashMap;
import java.util.Map;

import com.link.back.redmine.dto.Issue;
import com.link.back.redmine.dto.response.IssueListResponse;

public class IssueCountResponseDto {

	private Map<String, Integer> issueCount;

	public IssueCountResponseDto(IssueListResponse issueList, Long userId) {
		issueCount = new HashMap<>();
		// for (Issue issue : issueList.getIssues()) {
		// 	if(issue.get)
		// }

		// this.members = userTeamList.stream()
		// 	.collect(groupingBy(
		// 		userTeam -> userTeam.getMemberStatus().name(),
		// 		HashMap::new,
		// 		mapping(RecruitTeamMemberResponseDto::new, toList())
		// 	));
	}

}
