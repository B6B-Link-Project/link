package com.link.back.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.link.back.dto.request.ProjectRequestDto;
import com.link.back.dto.response.BackPerformanceMessageResponseDto;
import com.link.back.dto.response.BackPerformanceResponseDto;
import com.link.back.dto.response.ProjectDetailResponseDto;
import com.link.back.dto.response.ProjectResponseDto;
import com.link.back.infra.rabbitmq.RabbitPublisher;
import com.link.back.openfeign.dto.Contribution;
import com.link.back.security.JwtTokenProvider;
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
	private final JwtTokenProvider jwtTokenProvider;

	@Value("${GIT_PERSONAL_TOKEN}")
	private String gitAuthorization;

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

	@GetMapping("/{projectId}/message-count")
	public ResponseEntity<BackPerformanceMessageResponseDto> getMessageCount(@PathVariable Long projectId){
		BackPerformanceMessageResponseDto backPerformanceMessageResponseDto = backPerformanceService.getMessageCount(projectId);
		return new ResponseEntity<>(backPerformanceMessageResponseDto,HttpStatus.OK);
	}
	@GetMapping("/contributions/{owner}/{repo}")
	public ResponseEntity<List<Contribution>> getContributions(@PathVariable String owner, @PathVariable String repo) {
		List<Contribution> contributions = projectContributionService.getContributionsList(owner, repo);
		return new ResponseEntity<>(contributions, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<Page<ProjectResponseDto>> getAllProjects(
		@RequestHeader(value = "Authorization", required = false) String token, Pageable pageable) {
		Long myUserId = this.getUserIdFromToken(token);
		Page<ProjectResponseDto> allClosedProjects = projectService.getAllClosedProjects(myUserId, pageable);
		return new ResponseEntity<>(allClosedProjects, HttpStatus.OK);
	}

	@GetMapping("/my-project")
	public ResponseEntity<Page<ProjectResponseDto>> getMyProject(
		@RequestHeader(value = "Authorization", required = true) String token, Pageable pageable) {
		Long myUserId = this.getUserIdFromToken(token);
		Page<ProjectResponseDto> myProjects = projectService.getMyProjects(myUserId, pageable);
		return new ResponseEntity<>(myProjects, HttpStatus.OK);
	}

	@GetMapping("/{project_id}")
	public ResponseEntity<ProjectDetailResponseDto> getProjectDetail(
		@RequestHeader(value = "Authorization", required = false) String token,
		@PathVariable("project_id") Long projectId) {
		Long myUserId = this.getUserIdFromToken(token);
		ProjectDetailResponseDto projectDetail = projectService.getProjectDetail(myUserId, projectId);
		return new ResponseEntity<>(projectDetail, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<?> postProject(@RequestPart(value = "project") @NotNull ProjectRequestDto projectRequestDto,
		@RequestPart(value = "image", required = false) MultipartFile image) {
		projectService.createProject(projectRequestDto, image);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/{project_id}")
	public ResponseEntity<?> putProject(@PathVariable("project_id") Long projectId,
		@RequestPart(value = "project") @NotNull ProjectRequestDto projectRequestDto,
		@RequestPart(value = "image", required = false) MultipartFile image) {
		projectService.updateProject(projectId, projectRequestDto, image);
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
	public ResponseEntity<?> postProjectLike(@RequestHeader(value = "Authorization", required = true) String token,
		@PathVariable("project_id") Long projectId) {
		Long myUserId;
		try {
			myUserId = this.getUserIdFromToken(token);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		projectService.registerLike(myUserId, projectId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/{project_id}/like")
	public ResponseEntity<?> deleteProjectLike(@RequestHeader(value = "Authorization", required = true) String token,
		@PathVariable("project_id") Long projectId) {
		Long myUserId;
		try {
			myUserId = this.getUserIdFromToken(token);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		projectService.unregisterLike(myUserId, projectId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/like")
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Page<ProjectResponseDto>> getLikedProjects(
		@RequestHeader(value = "Authorization", required = true) String token, Pageable pageable) {
		Long myUserId;
		try {
			myUserId = this.getUserIdFromToken(token);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Page<ProjectResponseDto> likedProjects = projectService.getLikedProjects(myUserId, pageable);
		return new ResponseEntity<>(likedProjects, HttpStatus.OK);
	}

	@GetMapping("/popular")
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Page<ProjectResponseDto>> getPopularProjects(
		@RequestHeader(value = "Authorization", required = false) String token, Pageable pageable) {
		Long myUserId = this.getUserIdFromToken(token);
		Page<ProjectResponseDto> likedProjects = projectService.getPopularProjects(myUserId, pageable);
		return new ResponseEntity<>(likedProjects, HttpStatus.OK);
	}

	@GetMapping("/{project_id}/leader")
	public ResponseEntity<Boolean> isLeader(@RequestHeader(value = "Authorization", required = false) String token,
		@PathVariable Long project_id) {
		if (token == null)
			return new ResponseEntity<>(false, HttpStatus.OK);
		try {
			Long myUserId = this.getUserIdFromToken(token);
			return new ResponseEntity<>(projectService.checkLeader(myUserId, project_id), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(false, HttpStatus.OK);
		}
	}

	private Long getUserIdFromToken(String token) {
		if (token == null) return null;
		return jwtTokenProvider.getUserId(token);
	}

}
