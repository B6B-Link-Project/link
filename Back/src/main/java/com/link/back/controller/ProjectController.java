package com.link.back.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.link.back.dto.request.ProjectRequestDto;
import com.link.back.dto.response.BackPerformanceResponseDto;
import com.link.back.dto.response.ProjectDetailResponseDto;
import com.link.back.dto.response.ProjectResponseDto;
import com.link.back.infra.rabbitmq.RabbitPublisher;
import com.link.back.openfeign.dto.Contribution;
import com.link.back.service.BackPerformanceService;
import com.link.back.service.ProjectContributionService;
import com.link.back.service.ProjectService;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

	private final RabbitPublisher rabbitPublisher;
	private final ProjectContributionService projectContributionService;
	private final ProjectService projectService;
	private final BackPerformanceService backPerformanceService;

	@PostMapping("/{project_id}/back-metrics")
	public ResponseEntity<Void> sendMessage(@PathVariable Long project_id) {

		rabbitPublisher.sendMessages(project_id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{project_id}/back-metrics")
	public ResponseEntity<Page<BackPerformanceResponseDto>> getBackPerformances(@PathVariable Long project_id,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "1") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("backPerformanceId").descending());
		return new ResponseEntity<>(backPerformanceService.getBackPerformanceResponses(project_id, pageable),
			HttpStatus.OK);
	}

	@GetMapping("/contributions/{owner}/{repo}")
	public ResponseEntity<List<Contribution>> getContributions(@PathVariable String owner, @PathVariable String repo) {
		List<Contribution> contributions = projectContributionService.getContributionsList(owner, repo);
		return new ResponseEntity<>(contributions, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<Page<ProjectResponseDto>> getAllProjects(Pageable pageable) {
		Long userId = 1L; // FIXME: 토큰에서 내 아이디 가져오기
		Page<ProjectResponseDto> allClosedProjects = projectService.getAllClosedProjects(userId, pageable);
		return new ResponseEntity<>(allClosedProjects, HttpStatus.OK);
	}

	@GetMapping("/my-project")
	public ResponseEntity<List<ProjectResponseDto>> getMyProject() {
		Long myUserId = 1L; // FIXME: 토큰에서 내 아이디 가져오기
		List<ProjectResponseDto> myProjects = projectService.getMyProjects(myUserId);
		return new ResponseEntity<>(myProjects, HttpStatus.OK);
	}

	@GetMapping("/{project_id}")
	public ResponseEntity<ProjectDetailResponseDto> getProjectDetail(@PathVariable("project_id") Long projectId) {
		Long userId = 1L; // FIXME: 토큰에서 내 아이디 가져오기
		ProjectDetailResponseDto projectDetail = projectService.getProjectDetail(userId, projectId);
		return new ResponseEntity<>(projectDetail, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<?> postProject(@RequestBody @NotNull ProjectRequestDto projectRequestDto) {
		projectService.createProject(projectRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/{project_id}")
	public ResponseEntity<?> putProject(@PathVariable("project_id") Long projectId,
		@NotNull @RequestBody ProjectRequestDto projectRequestDto) {
		projectService.updateProject(projectId, projectRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/{project_id}")
	public ResponseEntity<?> deleteProject(@PathVariable("project_id") Long projectId) {
		projectService.deleteProject(projectId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/{project_id}/submit")
	public ResponseEntity<?> putProjectStatus(@PathVariable("project_id") Long projectId) {
		projectService.submitProject(projectId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/{project_id}/like")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> postProjectLike(@PathVariable("project_id") Long projectId) {
		Long userId = 1L; // FIXME: 토큰에서 내 아이디 가져오기
		projectService.registerLike(userId, projectId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/{project_id}/like")
	public ResponseEntity<?> deleteProjectLike(@PathVariable("project_id") Long projectId) {
		Long userId = 1L; // FIXME: 토큰에서 내 아이디 가져오기
		projectService.unregisterLike(userId, projectId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/like")
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<List<ProjectResponseDto>> getLikedProjects() {
		Long userId = 1L; // FIXME: 토큰에서 내 아이디 가져오기
		List<ProjectResponseDto> likedProjects = projectService.getLikedProjects(userId);
		return new ResponseEntity<>(likedProjects, HttpStatus.OK);
	}

	@GetMapping("/popular")
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<List<ProjectResponseDto>> getPopularProjects(@NotNull Pageable pageable) {
		Long userId = 1L; // FIXME: 토큰에서 내 아이디 가져오기
		List<ProjectResponseDto> likedProjects = projectService.getPopularProjects(userId);
		return new ResponseEntity<>(likedProjects, HttpStatus.OK);
	}

}
