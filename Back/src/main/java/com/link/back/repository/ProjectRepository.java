package com.link.back.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.link.back.entity.Project;
import com.link.back.entity.ProjectStatus;
import com.link.back.entity.User;

import feign.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {

	@Query("SELECT p"
		+ " FROM Project p JOIN p.team t JOIN UserTeam ut ON t = ut.team"
		+ " WHERE ut.user.userId = :userId AND ut.role != 'CANDIDATE'")
	Page<Project> findByUserId(Long userId, Pageable pageable);

	Page<Project> findAllByProjectStatusOrderByRegisteredDateDesc(ProjectStatus projectStatus, Pageable pageable);

	@Query("SELECT p"
		+ " FROM Project p LEFT JOIN ProjectLike pl ON p = pl.project"
		+ " WHERE p.projectStatus = :projectStatus"
		+ " GROUP BY p"
		+ " ORDER BY COUNT(pl.project) DESC")
	public Page<Project> findByProjectStatusOrderByLikesDesc(ProjectStatus projectStatus, Pageable pageable);

	@Query("delete from Project p where p.team.teamId = :teamId")
	void deleteByTeamId(@Param Long teamId);

	@Query("SELECT p"
		+ " FROM Project p JOIN ProjectLike pl ON p = pl.project"
		+ " WHERE pl.user = :user"
		+ " ORDER BY p.registeredDate DESC")
	Page<Project> findLikedProjectsByUser(User user, Pageable pageable);

	@Query("SELECT p " +
		"FROM Project p " +
		"JOIN p.team t ON p.team.teamId = t.teamId " +
		"JOIN Hackathon h ON h.hackathonId = :hackathonId " +
		"WHERE p.winState = true " +
		"and p.projectStatus = 'CLOSED' " +
		"ORDER BY p.hackathonScore DESC " +
	"limit 3")
	public List<Project> findProjectsByHackathonScoreAndWinState(Long hackathonId);

	@Query("SELECT p " +
		"FROM Project p " +
		"JOIN Hackathon h ON p.team.hackathon.hackathonId = :hackathonId " +
		"WHERE p.projectStatus = :status ")
	Page<Project> findByHackathonIdAndProjectStatus(Long hackathonId, ProjectStatus status, Pageable pageable);

	@Query("select p from Project p where p.team.teamId = :teamId")
	Project findByTeamId(Long teamId);
}