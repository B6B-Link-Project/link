package com.link.back.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.link.back.dto.RankingDTO;
import com.link.back.entity.User;

import feign.Param;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

	@Query("select user from User user"
		+ " join fetch UserSkill us on us.user = user"
		+ " join fetch Skill s on us.skill = s"
		+ " join fetch UserImage ui on user.userImage = ui"
		+ " where user.userId = :userId")
	User findUserInfoById(@Param("userId") Long userId);

	@Query("select user from User user"
		+ " join fetch user.userSkills us"
		+ " join fetch us.skill s"
		+ " left join fetch user.userImage ui" // left join을 사용하여 UserImage가 없는 경우도 조회되도록 함
		+ " where user.userId = :userId")
	User findMyDataById(@Param("userId") Long userId);

	//Boolean existsByEmail(String email);
	Optional<User> findByEmail(String email);
	Optional<User> findByUserId(Long userId);

	Optional<User> findByNameAndBirthAndPhoneNumber(String name, LocalDate birth, String phoneNumber);

	Optional<User> findByName(String name);

	@Query("SELECT new com.link.back.dto.RankingDTO(u.userId, u.name, u.introduce, u.rating, u.referenceUrl, img.userImageUrl) " +
		"FROM User u JOIN u.userImage img " +
		"ORDER BY u.rating DESC")
	List<RankingDTO> findOrderByRatingDesc(PageRequest pageRequest);

	/**
	 * 소셜 타입과 소셜의 식별값으로 회원 찾는 메소드
	 * 정보 제공을 동의한 순간 DB에 저장해야하지만, 아직 추가 정보(사는 도시, 나이 등)를 입력받지 않았으므로
	 * 유저 객체는 DB에 있지만, 추가 정보가 빠진 상태이다.
	 * 따라서 추가 정보를 입력받아 회원 가입을 진행할 때 소셜 타입, 식별자로 해당 회원을 찾기 위한 메소드
	 */
	//    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}

