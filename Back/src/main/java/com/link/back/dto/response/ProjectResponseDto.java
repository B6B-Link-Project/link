package com.link.back.dto.response;

import java.time.LocalDateTime;

import com.link.back.dto.ProjectImageDto;
import com.link.back.entity.ProjectStatus;

import lombok.Builder;

@Builder
public record ProjectResponseDto(
	Long projectId,
	String projectName,
	String projectDesc,
	Integer starCount,
	Boolean starred,
	String imgSrc
) {
}
