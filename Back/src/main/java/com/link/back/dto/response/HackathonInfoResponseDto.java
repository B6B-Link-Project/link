package com.link.back.dto.response;

import java.time.LocalDate;

import com.link.back.entity.Hackathon;

import lombok.Getter;

@Getter
public class HackathonInfoResponseDto {

	private final Long hackathonId;
	private final String hackathonName;
	private final String hackathonTopic;
	private final LocalDate registerDate;
	private final LocalDate teamDeadlineDate;
	private final LocalDate startDate;
	private final LocalDate endDate;
	private final String hackathonImageUrl;
	private final String hackathonOriginImageName;

	public HackathonInfoResponseDto(Hackathon hackathon) {
		this.hackathonId = hackathon.getHackathonId();
		this.hackathonName = hackathon.getHackathonName();
		this.hackathonTopic = hackathon.getHackathonTopic();
		this.registerDate = hackathon.getRegisterDate();
		this.teamDeadlineDate = hackathon.getTeamDeadlineDate();
		this.startDate = hackathon.getStartDate();
		this.endDate = hackathon.getEndDate();
		this.hackathonImageUrl = hackathon.getHackathonImage().getHackathonImageUrl();
		this.hackathonOriginImageName = hackathon.getHackathonImage().getHackathonOriginImageName();
	}
}
