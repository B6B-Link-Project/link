package com.link.back.entity;

import static com.link.back.config.AppConstant.*;
import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.link.back.dto.request.AdditionalUserInfoRequest;
import com.link.back.dto.request.UserUpdateInfoRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long userId;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "user_image_id")
	private UserImage userImage;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(length = PHONENUMBER_LENGTH, nullable = false)
	private String phoneNumber;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private boolean gender;

	@Column(nullable = false)
	private LocalDate birth;

	@Column(nullable = false)
	@ColumnDefault("0")
	private Integer rating; // 티어 점수

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean registered;

	private LocalDate registeredDate;

	private Integer career;

	private String referenceUrl;

	private String deployUrl;

	@Column(length = USER_INTRODUCE)
	private String introduce;

	@Enumerated(STRING)
	private Field field;

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean joinState; // 프로젝트 참가 여부

	public User(User user) {
		this.userId = user.userId;
		this.email = user.email;
		this.password = user.password;
		this.phoneNumber = user.phoneNumber;
		this.name = user.name;
		this.gender = user.gender;
		this.birth = user.birth;
		this.rating = user.rating;
		this.registered = user.registered;
		this.registeredDate = user.registeredDate;
		this.career = user.career;
		this.referenceUrl = user.referenceUrl;
		this.deployUrl = user.deployUrl;
		this.introduce = user.introduce;
		this.field = user.field;
		this.joinState = user.joinState;
	}

	//수정필요
	public void updateAdditionalUserInfo(List<UserSkill> userSkills, String referenceUrl, Field field, int career, UserImage userImage, Boolean registered){
		this.userSkills = userSkills;
		this.referenceUrl = referenceUrl;
		this.field = field;
		this.career = career;
		this.userImage = userImage;
		this.registered = registered;
	}


	// rating 시작을 일단 0으로
	@Builder
	public User(String email, String password, String name, boolean gender, LocalDate birth, String phoneNumber,
		int career) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.gender = gender;
		this.birth = birth;
		this.phoneNumber = phoneNumber;
		this.career = career;
		this.rating = 0;
	}

	public void updateUser(UserImage userImage, List<UserSkill> userSkills,  UserUpdateInfoRequest userUpdateInfoRequest){
		// this.password = new BCryptPasswordEncoder().encode(userUpdateInfoRequest.getPassword());
		this.phoneNumber = userUpdateInfoRequest.getPhoneNumber();
		this.name = userUpdateInfoRequest.getName();
		this.birth = userUpdateInfoRequest.getBirth();
		this.gender = userUpdateInfoRequest.isGender();
		this.userSkills = userSkills;
		this.registeredDate = userUpdateInfoRequest.isRegistered()? LocalDate.now():null;
		this.introduce = userUpdateInfoRequest.getIntroduce();
		//dto 만들기
		this.userImage = userImage;
		this.referenceUrl = userUpdateInfoRequest.getReferenceUrl();
		this.field = userUpdateInfoRequest.getField();
		this.career = userUpdateInfoRequest.getCareer();
		this.registered = userUpdateInfoRequest.isRegistered();
	}

	public void addUserInfo (UserImage userImage, List<UserSkill> userSkills,  AdditionalUserInfoRequest additionalUserInfoRequest){

		this.userSkills = userSkills;
		this.registeredDate = additionalUserInfoRequest.isRegistered()? LocalDate.now():null;
		this.introduce = additionalUserInfoRequest.getIntroduce();
		//dto 만들기
		this.userImage = userImage;
		this.referenceUrl = additionalUserInfoRequest.getReferenceUrl();
		this.career = additionalUserInfoRequest.getCareer();
		this.field = additionalUserInfoRequest.getField();
		this.registered = additionalUserInfoRequest.isRegistered();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	//비밀번호 변경 메소드

	public void updatePassword(String password) {
		this.password = new BCryptPasswordEncoder().encode(password);
	}

	@OneToMany(mappedBy = "user")
	// @BatchSize(size = 100) // todo: AppConstant.USER_USER_SKILLS_BATCH_SIZE
	List<UserSkill> userSkills = new ArrayList<>();

	public void changeJoinState() {
		this.joinState = false;
	}

	public boolean getJoinStatus() {
		return joinState;
	}

	public void startProject() {
		this.joinState = true;
	}
}


