package com.link.back.dto.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.link.back.redmine.dto.Issue;
import com.link.back.redmine.dto.response.IssueListResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueCountResponseDto {

	private final Map<String, Integer> issueCount;

	public IssueCountResponseDto(IssueListResponse issueList, Long userId) {
		this.issueCount = new HashMap<>();
		for (Issue issue : issueList.getIssues()) {
			if(issue.getAssignedTo() != null) {
				if(issue.getAssignedTo().getId() == userId) {
					Integer cnt = issueCount.getOrDefault(issue.getStatus().getName(), 0);
					issueCount.put(issue.getStatus().getName(), ++cnt);
				}
			}
		}
	}

}
