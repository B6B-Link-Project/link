package com.link.back.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.link.back.entity.Project;
import com.link.back.entity.ProjectStatus;
import com.link.back.entity.User;

import feign.Param;
import jakarta.transaction.Transactional;

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
		"WHERE p.projectStatus = :status "
		+ "ORDER BY p.hackathonScore DESC ")
	Page<Project> findByHackathonIdAndProjectStatus(Long hackathonId, ProjectStatus status, Pageable pageable);

	@Query("select p from Project p where p.team.teamId = :teamId")
	Project findByTeamId(Long teamId);

	@Modifying
	@Transactional
	@Query(nativeQuery = true, value =
		"UPDATE project pj " +
			"JOIN ( " +
			"    SELECT l.project_id AS project_id, " +
			"           ROUND(((l.lighthouse_score + b.backend_performance) / 2), 2) AS average_score " +
			"    FROM ( " +
			"        SELECT a.project_id AS project_id, ROUND(AVG(a.average_score), 0) AS lighthouse_score " +
			"        FROM ( " +
			"            SELECT (((((JSON_EXTRACT(commonlink.lighthouse_report.report, '$.PWA.score') + " +
			"                        JSON_EXTRACT(commonlink.lighthouse_report.report, '$.\"검색엔진 최적화\".score')) + " +
			"                       JSON_EXTRACT(commonlink.lighthouse_report.report, '$.\"접근성\".score')) + " +
			"                      JSON_EXTRACT(commonlink.lighthouse_report.report, '$.\"권장사항\".score')) + " +
			"                     JSON_EXTRACT(commonlink.lighthouse_report.report, '$.\"성능\".score')) * 20) AS average_score, " +
			"                   commonlink.lighthouse_report.project_id AS project_id " +
			"            FROM commonlink.lighthouse_report " +
			"        ) a " +
			"        GROUP BY a.project_id " +
			"    ) l " +
			"    JOIN ( " +
			"        SELECT b.project_id AS project_id, " +
			"               ROUND(((((calculateGrade((100 - b.bugs)) + " +
			"                         calculateGrade(((500 - b.code_smells) / 5))) + " +
			"                        (b.coverage * 100)) + " +
			"                       calculateGrade((100 - b.duplications))) + " +
			"                      (b.security_rating * 100)) / 5, 0) AS backend_performance " +
			"        FROM ( " +
			"            SELECT bp.project_id AS project_id, " +
			"                   bp.bugs, bp.code_smells, bp.coverage, bp.duplications, bp.security_rating " +
			"            FROM commonlink.back_performance bp " +
			"            INNER JOIN ( " +
			"                SELECT project_id, MAX(commonlink.back_performance.back_performance_id) AS max_id " +
			"                FROM commonlink.back_performance " +
			"                GROUP BY project_id " +
			"            ) max_bp ON bp.project_id = max_bp.project_id AND bp.back_performance_id = max_bp.max_id " +
			"        ) b " +
			"    ) b ON l.project_id = b.project_id " +
			") AS new_scores ON pj.project_id = new_scores.project_id " +
			"SET pj.hackathon_score = new_scores.average_score"
	)
	void updateHackathonScore();
}