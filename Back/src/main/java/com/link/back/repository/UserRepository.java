package com.link.back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.link.back.entity.User;

import feign.Param;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

	@Query("select user from User user"
		+ " join fetch UserSkill us"
		+ " join fetch Skill s"
		+ " join fetch UserImage ui"
		+ " where user.userId = :userId")
	User findUserInfoById(@Param("userId") Long userId);
	//    Boolean existsByEmail(String email);
	Optional<User> findByEmail(String email);
	Optional<User> findByUserId(Long userId);

	//    Optional<User> findByNickname(String nickname);


	//    @Transactional
	//    Long deleteByEmail(String email);

	/**
	 * 소셜 타입과 소셜의 식별값으로 회원 찾는 메소드
	 * 정보 제공을 동의한 순간 DB에 저장해야하지만, 아직 추가 정보(사는 도시, 나이 등)를 입력받지 않았으므로
	 * 유저 객체는 DB에 있지만, 추가 정보가 빠진 상태이다.
	 * 따라서 추가 정보를 입력받아 회원 가입을 진행할 때 소셜 타입, 식별자로 해당 회원을 찾기 위한 메소드
	 */
	//    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

}

