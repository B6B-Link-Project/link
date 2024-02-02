package com.link.back.dto.response;

import java.time.LocalDateTime;

import com.link.back.dto.ProjectImageDto;
import com.link.back.entity.ProjectStatus;

import lombok.Builder;

@Builder
public record ProjectDetailResponseDto(
	Long projectId,
	Long teamId, // Team team,
	Long hackathonId,
	String hackathonName,
	String hackathonTopic,
	Integer hackathonScore,
	String projectName,
	String projectDesc,
	ProjectStatus projectStatus,
	LocalDateTime registeredDate,
	String projectUrl,
	String deployUrl,
	Boolean winState,
	String imsSrc,
	Integer starCount,
	Boolean starred
) {
}
