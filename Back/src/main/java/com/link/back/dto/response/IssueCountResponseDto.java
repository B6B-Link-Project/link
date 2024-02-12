package com.link.back.dto.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.link.back.redmine.dto.Issue;
import com.link.back.redmine.dto.response.IssueListResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueCountResponseDto {

	private final Map<Object, Long> issueCount;

	public IssueCountResponseDto(IssueListResponse issueList, Long userId) {
		this.issueCount = issueList.getIssues().stream()
			.filter(issue -> issue.getAssignedTo().getId() == userId)
			.collect(Collectors.groupingBy(
				issue -> issue.getStatus().getName(),
				Collectors.counting()
			));
	}

}
