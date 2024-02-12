package com.link.back.service;

import org.springframework.stereotype.Service;

import com.link.back.redmine.service.RedmineProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IssueService {

	private final RedmineProjectService redmineProjectService;



}
