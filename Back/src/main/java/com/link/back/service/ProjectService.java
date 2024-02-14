package com.link.back.service;

// import static com.link.back.common.mapper.ProjectMapper.*;

import static com.link.back.entity.MemberStatus.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.link.back.config.S3Uploader;
import com.link.back.dto.Image;
import com.link.back.dto.ProjectImageDto;
import com.link.back.dto.request.ProjectRequestDto;
import com.link.back.dto.response.ProjectDetailResponseDto;
import com.link.back.dto.response.ProjectResponseDto;
import com.link.back.entity.Project;
import com.link.back.entity.ProjectImage;
import com.link.back.entity.ProjectLike;
import com.link.back.entity.ProjectStatus;
import com.link.back.entity.Role;
import com.link.back.entity.Team;
import com.link.back.entity.User;
import com.link.back.entity.UserTeam;
import com.link.back.exception.ContentNotFoundException;
import com.link.back.redmine.dto.request.CreateProjectRequest;
import com.link.back.redmine.dto.request.CreateUserRequest;
import com.link.back.redmine.service.RedmineProjectService;
import com.link.back.repository.ProjectImageRepository;
import com.link.back.repository.ProjectLikeRepository;
import com.link.back.repository.ProjectRepository;
import com.link.back.repository.TeamRepository;
import com.link.back.repository.UserRepository;
import com.link.back.repository.UserTeamRepository;

import lombok.RequiredArgsConstructor;

@EnableScheduling
@Service
@RequiredArgsConstructor
public class ProjectService {

	// TODO: 프로젝트 이미지 관련 로직 검증

	private final ProjectRepository projectRepository;
	private final TeamRepository teamRepository;
	private final ProjectImageRepository projectImageRepository;
	private final RedmineProjectService redmineProjectService;
	private final UserTeamRepository userTeamRepository;
	private final ProjectLikeRepository projectLikeRepository;
	private final UserRepository userRepository;
	private final S3Uploader s3Uploader;

	// TODO: 팀 생성시 프로젝트 자동 생성 되도록
	@Transactional
	public void createProject(ProjectRequestDto projectRequestDto, MultipartFile image) {
		// TODO: 프로젝트 이미지 받아와서 업로드
		ProjectImage projectImage = uploadImage(image);
		Team team = teamRepository.getReferenceById(projectRequestDto.teamId());
		team.makeProject();
		List<UserTeam> userTeamList = team.getUserTeamList();
		for (UserTeam userTeam : userTeamList) {
			if(userTeam.getMemberStatus() == JOINED) {
				userTeam.getUser().startProject();
			} else {
				userTeamRepository.delete(userTeam);
			}
		}
		Project project = createProjectEntity(team, projectImage, projectRequestDto);
		Project save = projectRepository.save(project);

		try {
			// 레드마인 프로젝트 생성
			CreateProjectRequest cpr = CreateProjectRequest.builder()
				.name(projectRequestDto.projectName())
				.identifier("redmine" + save.getProjectId())
				.build();
			redmineProjectService.createProject(cpr);

			// 레드마인 유저 생성
			List<String> logins = userTeamRepository.findMembersByTeamId(projectRequestDto.teamId())
				.stream()
				.map(userTeam -> {
					redmineProjectService.createUser(new CreateUserRequest(userTeam.getUser(), project));
					return new CreateUserRequest(userTeam.getUser(), project).getLogin();
				})
				.collect(Collectors.toList());

			// 레드마인 프로젝트 유저 등록
			redmineProjectService.addProjectMembers(logins, cpr.getIdentifier());
		} catch (RuntimeException re) {
			if (projectImage != null)
				s3Uploader.deleteFile(projectImage.getProjectImageName()); // 이미 올라간 S3 이미지 삭제
			throw re;
		}

	}

	public Page<ProjectResponseDto> getAllClosedProjects(Long userId, Pageable pageable) {
		User user = userId != null ? userRepository.getReferenceById(userId) : null;
		Page<Project> projectPage = projectRepository.findAllByProjectStatusOrderByRegisteredDateDesc(
			ProjectStatus.CLOSED,
			pageable);
		List<ProjectResponseDto> projectResponseList = projectPage.stream()
			.map(p -> toProjectResponseDto(p, user))
			.toList();
		return new PageImpl<>(projectResponseList, projectPage.getPageable(), projectPage.getTotalElements());
	}

	public Page<ProjectResponseDto> getPopularProjects(Long userId, Pageable pageable) {
		User user = userId != null ? userRepository.getReferenceById(userId) : null;
		Page<Project> projectPage = projectRepository.findByProjectStatusOrderByLikesDesc(ProjectStatus.CLOSED,
			pageable);
		return new PageImpl<>(projectPage.stream().map(p -> toProjectResponseDto(p, user)).toList(),
			projectPage.getPageable(), projectPage.getTotalElements());
	}

	public Page<ProjectResponseDto> getMyProjects(Long userId, Pageable pageable) {
		User user = userId != null ? userRepository.getReferenceById(userId) : null;
		Page<Project> projectPage = projectRepository.findByUserId(userId, pageable);

		return new PageImpl<>(projectPage.stream().map(p -> toProjectResponseDto(p, user)).toList(),
			projectPage.getPageable(),
			projectPage.getTotalElements());
	}

	public ProjectDetailResponseDto getProjectDetail(Long userId, Long projectId) {
		// TODO: 진행중인 프로젝트이면 팀원만 조회 허용
		User user = userId != null ? userRepository.getReferenceById(userId) : null;
		Project project = projectRepository.findById(projectId).orElseThrow();
		return toProjectDetailResponseDto(project, user);
	}

	@Transactional
	public void updateProject(Long projectId, ProjectRequestDto projectRequestDto, MultipartFile image) {
		Project project = projectRepository.findById(projectId).orElseThrow();
		ProjectImage projectImage = uploadImage(image);
		if (projectImage == null) {
			projectImage = project.getProjectImage();
		}
		updateProjectEntity(project, projectImage, projectRequestDto);
		try {
			projectRepository.save(project);
		} catch (RuntimeException re) { // 저장 실패시 S3에 업로드된 이미지 삭제
			if (projectImage != null)
				s3Uploader.deleteFile(projectImage.getProjectImageName()); // 이미 올라간 S3 이미지 삭제
			throw re;
		}
	}

	@Transactional
	public void deleteProject(Long projectId) {
		Project project = projectRepository.getReferenceById(projectId);
		String imageName = null;
		if (project.getProjectImage() != null)
			imageName = project.getProjectImage().getProjectImageName();
		projectRepository.deleteById(projectId);
		if (imageName != null)
			s3Uploader.deleteFile(imageName); // S3 이미지 제거
	}

	@Transactional
	public void submitProject(Long projectId) {
		Project project = projectRepository.findById(projectId).orElseThrow();
		project.updateProjectStatus();
		projectRepository.save(project);
	}

	@Transactional
	public void registerLike(Long userId, Long projectId) {
		User user = userRepository.getReferenceById(userId);
		Project project = projectRepository.getReferenceById(projectId);
		Optional<ProjectLike> projectLikeOptional = projectLikeRepository.findByProjectAndUser(project, user);
		if (projectLikeOptional.isEmpty()) {
			ProjectLike newProjectLike = ProjectLike.builder()
				.user(user)
				.project(project)
				.build();
			projectLikeRepository.save(newProjectLike);
		}
	}

	@Transactional
	public void unregisterLike(Long userId, Long projectId) {
		User user = userRepository.getReferenceById(userId);
		Project project = projectRepository.getReferenceById(projectId);
		Optional<ProjectLike> projectLikeOptional = projectLikeRepository.findByProjectAndUser(project, user);
		projectLikeOptional.ifPresent(projectLikeRepository::delete);
	}

	public Page<ProjectResponseDto> getLikedProjects(Long userId, Pageable pageable) {
		User user = userRepository.getReferenceById(userId);
		Page<Project> projectPage = projectRepository.findLikedProjectsByUser(user, pageable);
		return new PageImpl<>(projectPage.stream().map(p -> toProjectResponseDto(p, user)).toList(),
			projectPage.getPageable(), projectPage.getTotalElements());
	}


	private ProjectResponseDto toProjectResponseDto(Project project, User user) {
		return ProjectResponseDto.builder()
			.projectId(project.getProjectId())
			.projectName(project.getProjectName())
			.projectDesc(project.getProjectDesc())
			.starCount(projectLikeRepository.countByProject(project))
			.starred(projectLikeRepository.findByProjectAndUser(project, user).isPresent())
			.imgSrc(project.getProjectImage() == null ? null : project.getProjectImage().getProjectImageUrl())
			.build();
	}

	private ProjectDetailResponseDto toProjectDetailResponseDto(Project project, User user) {
		return ProjectDetailResponseDto.builder()
			.projectId(project.getProjectId())
			.teamId(project.getTeam().getTeamId())
			.hackathonId(project.getTeam().getHackathon().getHackathonId())
			.hackathonName(project.getTeam().getHackathon().getHackathonName())
			.hackathonTopic("asd")
			.hackathonScore(project.getHackathonScore())
			.projectName(project.getProjectName())
			.projectDesc(project.getProjectDesc())
			.projectStatus(project.getProjectStatus())
			.registeredDate(project.getRegisteredDate())
			.projectUrl(project.getProjectUrl())
			.deployUrl(project.getDeployUrl())
			.winState(project.getWinState())
			.imgSrc(project.getProjectImage() != null ? project.getProjectImage().getProjectImageUrl() : null)
			.starCount(projectLikeRepository.countByProject(project))
			.starred(projectLikeRepository.findByProjectAndUser(project, user).isPresent())
			.build();
	}

	private ProjectImageDto toProjectImageDto(ProjectImage projectImage) {
		return ProjectImageDto.builder()
			.projectImgName(projectImage.getProjectImageName())
			.projectImgUrl(projectImage.getProjectImageUrl())
			.projectOriginImgName(projectImage.getProjectOriginImageName())
			.build();
	}

	private Project createProjectEntity(Team team, ProjectImage projectImage, ProjectRequestDto projectRequestDto) {
		return Project.builder()
			.team(team)
			.projectImage(projectImage)
			.projectName(projectRequestDto.projectName())
			.projectDesc(projectRequestDto.projectDesc())
			.projectStatus(ProjectStatus.OPENED)
			.registeredDate(LocalDateTime.now())
			.projectUrl(projectRequestDto.projectUrl())
			.hackathonScore(0)
			.winState(Boolean.FALSE)
			.deployUrl(null)
			.build();
	}

	private void updateProjectEntity(Project project, ProjectImage projectImage, ProjectRequestDto projectRequestDto) {
		project.updateProjectDetail(
			projectRequestDto.projectDesc(),
			projectRequestDto.projectUrl(),
			projectRequestDto.deployUrl(),
			projectImage
		);
	}

	private ProjectImage uploadImage(MultipartFile image) {
		ProjectImage projectImage = null;
		if (image != null) {
			try {
				Image img = s3Uploader.upload(image, "static");
				projectImage = ProjectImage.create(img.getImageName(), img.getImageUrl(), img.getOriginName());
				projectImageRepository.save(projectImage);
			} catch (IOException e) {
				throw new RuntimeException("S3 이미지 업로드 실패"); // FIXME: 예외 클래스 필요
			}
		}
		return projectImage;
	}

	// 매일 자정에 실행되도록 스케줄링합니다.
	@Scheduled(cron = "0 0 0 * * *")
	public void updateHackathonScoresDaily() {
		// 프로젝트의 hackathon_score를 업데이트하는 메서드를 호출합니다.
		projectRepository.updateHackathonScore();
	}

	public Boolean checkLeader(Long myUserId, Long project_id) {
		List<UserTeam> userTeamsByUserId = userTeamRepository.findUserTeamsByUserId(myUserId);
		if (userTeamsByUserId.isEmpty())
			return false;
		Optional<Project> project = projectRepository.findById(project_id);
		if (project.isEmpty())
			throw new ContentNotFoundException();
		for (UserTeam ut: userTeamsByUserId) {
			if(ut.getRole() == Role.LEADER && project.get().getTeam().getUserTeamList().contains(ut))
				return true;
		}
		return false;
	}

	public boolean isMyProject(Long projectId, Long userId) {
		return projectRepository.findById(projectId)
			.map(project -> project.getTeam().getUserTeamList().stream()
				.anyMatch(userTeam -> userTeam.getUser().getUserId().equals(userId)
					&& userTeam.getMemberStatus() == JOINED))
			.orElse(false);
	}
}
