package com.link.back.service;

import static com.link.back.entity.MemberStatus.*;
import static com.link.back.entity.Role.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.link.back.dto.response.CandidatesResponseDto;
import com.link.back.dto.response.MembersResponseDto;
import com.link.back.dto.response.TeamIdsResponseDto;
import com.link.back.entity.Team;
import com.link.back.entity.User;
import com.link.back.entity.UserTeam;
import com.link.back.repository.TeamRepository;
import com.link.back.repository.UserRepository;
import com.link.back.repository.UserTeamRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamBuildingSuggestionService {

	// private final TeamRepository teamRepository;
	// private final UserRepository userRepository;
	// private final UserTeamRepository userTeamRepository;
	//
	// public void suggestTeamToUser(Long teamId, Long userId) {
	// 	Team team = teamRepository.findById(teamId)
	// 		.orElseThrow(RuntimeException::new);// todo: create exception
	//
	// 	User user = userRepository.findById(userId)
	// 		.orElseThrow(RuntimeException::new); // todo: create exception
	//
	// 	UserTeam userTeam = UserTeam.builder()
	// 		.user(user)
	// 		.team(team)
	// 		.role(CANDIDATE)
	// 		.memberStatus(SUGGESTED)
	// 		.build();
	//
	// 	userTeamRepository.save(userTeam);
	//
	// 	// todo: notification!!!
	// }
	//
	// public void cancelSuggestion(Long teamId, Long userId) {
	// 	Team team = teamRepository.findById(teamId)
	// 		.orElseThrow(RuntimeException::new);// todo: create exception
	//
	// 	User user = userRepository.findById(userId)
	// 		.orElseThrow(RuntimeException::new); // todo: create exception
	//
	// 	userTeamRepository.deleteUserTeamByTeamAndUser(team, user);
	//
	// 	// todo: notification!!!
	// }
	//
	// public void acceptSuggestion(Long teamId, Long userId) {
	// 	Team team = teamRepository.findById(teamId)
	// 		.orElseThrow(RuntimeException::new);// todo: create exception
	//
	// 	User user = userRepository.findById(userId)
	// 		.orElseThrow(RuntimeException::new); // todo: create exception
	//
	// 	UserTeam userTeam = userTeamRepository.findUserTeamByTeamAndUser(team, user);
	// 	userTeam.joinTeam();
	//
	// 	// todo: notification!!!
	// }
	//
	// public void refuseSuggestion(Long teamId, Long userId) {
	// 	Team team = teamRepository.findById(teamId)
	// 		.orElseThrow(RuntimeException::new);// todo: create exception
	//
	// 	User user = userRepository.findById(userId)
	// 		.orElseThrow(RuntimeException::new); // todo: create exception
	//
	// 	userTeamRepository.deleteUserTeamByTeamAndUser(team, user);
	//
	// 	// todo: notification!!!
	// }
	//
	// @Transactional(readOnly = true)
	// public CandidatesResponseDto getSuggestionListOfTeam(Long teamId) {
	// 	Team team = teamRepository.findById(teamId)
	// 		.orElseThrow(RuntimeException::new); // todo: create exception
	//
	// 	List<UserTeam> candidates = userTeamRepository.findCandidates(team, SUGGESTED);
	//
	// 	return new CandidatesResponseDto(candidates);
	// }
	//
	// @Transactional(readOnly = true)
	// public MembersResponseDto getSuggestionListOfUser(Long teamId, Long userId) {
	// 	Team team = teamRepository.findById(teamId)
	// 		.orElseThrow(RuntimeException::new);// todo: create exception
	//
	// 	List<UserTeam> members = userTeamRepository.findMembers(team);
	//
	// 	return new MembersResponseDto(members);
	// }
	//
	// @Transactional(readOnly = true)
	// public TeamIdsResponseDto getSuggestionListOfUser(Long userId) {
	// 	User user = userRepository.findById(userId)
	// 		.orElseThrow(RuntimeException::new); // todo: create exception
	//
	// 	List<UserTeam> suggestedTeams = userTeamRepository.findTeamsOfCandidate(user, SUGGESTED);
	//
	// 	return new TeamIdsResponseDto(suggestedTeams);
	// }
}
