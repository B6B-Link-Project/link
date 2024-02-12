package com.link.back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.link.back.entity.MemberStatus;
import com.link.back.entity.Team;
import com.link.back.entity.User;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom {

	@Query("select t from Team t join fetch UserTeam u on t.teamId = u.team.teamId where u.user = :user and u.memberStatus = :status")
	List<Team> findTeamByUserAndStatus(@Param("user") User user, @Param("status") MemberStatus status);

	@Query("select t from Team t join UserTeam ut on ut.team = t where t.teamStatus = 'BUILDING' and ut.user = :user and ut.role = 'CANDIDATE'")
	List<Team> findBuildingTeamsByUser(@Param("user") User user);

	@Query("select t from Team t join UserTeam ut on ut.team = t where t.teamStatus = 'BUILDING' and ut.user = :user and ut.role = 'LEADER'")
	Team findActiveTeamByUser(@Param("user") User user);

	@Query("select t from Team t where t.teamStatus = 'BUILDING' order by t.teamId limit 6")
	List<Team> findAllTeam();
}