package com.link.back.redmine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.link.back.redmine.dto.AssignedTo;
import com.link.back.redmine.dto.Author;
import com.link.back.redmine.dto.Priority;
import com.link.back.redmine.dto.Project;
import com.link.back.redmine.dto.Status;
import com.link.back.redmine.dto.Tracker;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {

	private int id;
	private Project project;
	private Tracker tracker;
	private Status status;
	private Priority priority;
	private Author author;
	@JsonProperty("assigned_to")
	private AssignedTo assignedTo;
	private String subject;
	private String description;


}