package com.link.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.link.back.entity.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {
}
