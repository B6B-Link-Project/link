package com.link.back.controller;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.link.back.dto.response.IssueCountResponseDto;
import com.link.back.entity.Project;
import com.link.back.entity.User;
import com.link.back.redmine.dto.request.CreateIssueRequest;
import com.link.back.redmine.dto.request.UpdateIssueRequest;
import com.link.back.redmine.dto.response.IssueListResponse;
import com.link.back.redmine.dto.response.ProjectListResponse;
import com.link.back.redmine.service.RedmineProjectService;
import com.link.back.repository.ProjectRepository;
import com.link.back.repository.UserRepository;
import com.link.back.security.JwtTokenProvider;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/project")
@RequiredArgsConstructor
public class IssueController {

	private final RedmineProjectService redmineProjectService;
	private final ProjectRepository projectRepository;
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;

	@GetMapping("/{projectId}/issue")
	@ResponseStatus(OK)
	public IssueCountResponseDto getIssueList(@PathVariable Long projectId, @RequestHeader("Authorization") String token) {
		Project project = projectRepository.findById(projectId).orElseThrow(RuntimeException::new);
		Long redMineProjectId = redmineProjectService.findProjectId(project.getProjectName() + projectId);
		IssueListResponse issueList = redmineProjectService.findIssueList(redMineProjectId);
		User user = userRepository.findById(jwtTokenProvider.getUserId(token)).orElseThrow(RuntimeException::new);
		String login = user.getField() + user.getName();
		Long redmineUserId = redmineProjectService.findUserId(login);
		return new IssueCountResponseDto(issueList, redmineUserId);
	}

	// @GetMapping("/{projectId}/issue")
	// @ResponseStatus(OK)
	// public IssueListResponse getIssueList(@PathVariable Long projectId) {
	// 	// Project project = projectRepository.findById(projectId).orElseThrow(RuntimeException::new);
	// 	// Long redMineProjectId = redmineProjectService.findProjectId(project.getProjectName() + projectId);
	// 	return redmineProjectService.findIssueList(projectId);
	// }

	@PostMapping("/{projectId}/issue/{assignedToId}")
	@ResponseStatus(CREATED)
	public void createIssue(@RequestBody String subject, @PathVariable Long projectId,
		@PathVariable Long assignedToId) {
		Project project = projectRepository.findById(projectId).orElseThrow(RuntimeException::new);
		User user = userRepository.findById(assignedToId).orElseThrow(RuntimeException::new);
		redmineProjectService.createIssue(
			new CreateIssueRequest(subject), user.getField() + user.getName(),
			project.getProjectName() + projectId);
	}

	@DeleteMapping("/{issueId}")
	@ResponseStatus(OK)
	public void removeIssue(@PathVariable Long issueId) {
		redmineProjectService.removeIssue(issueId);
	}

	@PutMapping("/{issueId}")
	@ResponseStatus(OK)
	public void updateIssue(@RequestBody UpdateIssueRequest uir, @PathVariable Long issueId) {
		redmineProjectService.updateIssue(uir, issueId);
	}

}
