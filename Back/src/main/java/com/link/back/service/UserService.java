package com.link.back.service;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.link.back.dto.GetUserBeforeInfoDTO;
import com.link.back.dto.JwtToken;
import com.link.back.dto.LoginRequest;
import com.link.back.dto.RankingDTO;
import com.link.back.dto.UserSignUpDto;
import com.link.back.dto.VerificationCode;
import com.link.back.dto.request.AdditionalUserInfoRequest;
import com.link.back.dto.request.UseApiRequest;
import com.link.back.dto.request.UserPasswordResetRequest;
import com.link.back.dto.request.UserUpdateInfoRequest;
import com.link.back.dto.response.ApiDetailResponse;
import com.link.back.dto.response.CareerApiResponse;
import com.link.back.dto.response.UserInfoResponsse;
import com.link.back.entity.Skill;
import com.link.back.entity.User;
import com.link.back.entity.UserImage;
import com.link.back.entity.UserSkill;
import com.link.back.repository.RefreshTokenRepository;
import com.link.back.repository.SkillRepository;
import com.link.back.repository.UserImageRepository;
import com.link.back.repository.UserRepository;
import com.link.back.repository.UserSkillRepository;
import com.link.back.repository.VerificationCodeRepository;
import com.link.back.security.JwtTokenProvider;

import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final VerificationCodeRepository verificationCodeRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final UserImageRepository userImageRepository;
	private final UserSkillRepository userSkillRepository;
	private final SkillRepository skillRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;
	private final JavaMailSender javaMailSender;

	@Value("${codef.key}")
	private String codefKey;
	@Value("${codef.demo.Client.id}")
	private String codefId;
	@Value("${codef.demo.Client.Secret}")
	private String codefSecret;

	//커리어 추가
	public String signup(UserSignUpDto userSignUpDto) throws Exception {

		if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
			throw new Exception("이미 존재하는 이메일입니다.");
		}

		User user = User.builder()
			.email(userSignUpDto.getEmail())
			.password(passwordEncoder.encode(userSignUpDto.getPassword()))
			.name(userSignUpDto.getName())
			.gender(userSignUpDto.isGender())
			.birth(userSignUpDto.getBirth())
			.phoneNumber(userSignUpDto.getPhoneNumber())
			.career(userSignUpDto.getCareer())
			.build();

		userRepository.save(user);
		return "회원가입 성공";

	}

	public JwtToken login(LoginRequest loginRequest) {
		User user = userRepository.findByEmail(loginRequest.getEmail())
			.orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));

		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("잘못된 비밀번호입니다.");
		}

		JwtToken jwtToken = jwtTokenProvider.generateToken(user.getUserId());

		return jwtToken;
	}

	public String oauth2Token(String refreshToken) {
		String accessToken = jwtTokenProvider.generateOauth2token(refreshToken);

		return accessToken;
	}

	//이메일 찾기
	//이름, 생일, 전화번호 받음
	public String findEmail(String name, LocalDate birth, String phoneNumber) {

		if (userRepository.findByNameAndBirthAndPhoneNumber(name, birth, phoneNumber).isEmpty())
			return "NotFound";

		User user = userRepository.findByNameAndBirthAndPhoneNumber(name, birth, phoneNumber).get();

		String email = user.getEmail();

		return email;
	}

	@Transactional
	public void sendVerificationEmail(String email) {

		Optional<User> user = userRepository.findByEmail(email);

		//존재하지 않는 이메일이면
		if (user.isEmpty()) {
			throw new IllegalArgumentException("이메일이 존재하지 않습니다.");
		}

		//이메일에 인증번호보내기
		//1. 인증번호 생성(6자리)
		StringBuilder verificationCode = new StringBuilder();

		Random random = new Random();

		for (int i = 0; i < 6; i++) {
			int tmp = random.nextInt(10);
			verificationCode.append(tmp);
		}

		//2. 인증번호 보내기(messageAPI 사용)
		javaMailSender.send(new MimeMessagePreparator() {

			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
				messageHelper.setFrom("dlawhdfbf12@gmail.com");
				messageHelper.setTo(email);
				messageHelper.setSubject("이메일 인증번호");//일단 새 비밀번호로
				messageHelper.setText(verificationCode.toString());
			}
		});

		//3. 인증번호와 이메일을 redis에 저장
		verificationCodeRepository.save(new VerificationCode(verificationCode.toString(), email));

	}

	//이메일 인증 - 회원가입시
	@Transactional
	public void sendVerificationSignUpEmail(String email) {

		//이메일에 인증번호보내기
		//1. 인증번호 생성(6자리)
		StringBuilder verificationCode = new StringBuilder();

		Random random = new Random();

		for (int i = 0; i < 6; i++) {
			int tmp = random.nextInt(10);
			verificationCode.append(tmp);
		}

		//2. 인증번호 보내기(messageAPI 사용)
		javaMailSender.send(new MimeMessagePreparator() {

			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
				messageHelper.setFrom("dlawhdfbf12@gmail.com");
				messageHelper.setTo(email);
				messageHelper.setSubject("이메일 인증번호");//일단 새 비밀번호로

				// HTML 형식으로 메일 내용을 작성
				String verificationLink =
					"http://localhost:8080/api/users/signup/email/verification?verificationCode=" + verificationCode
						+ "&email=" + email.split("@")[0] + "%40" + email.split("@")[1];
				String emailContent = "<html><body><p>이메일 인증을 위한 링크를 클릭하세요:</p>"
					+ "<a href='" + verificationLink + "'>" + verificationLink + "</a></body></html>";

				messageHelper.setText(emailContent, true);
			}
		});

		//3. 인증번호와 이메일을 redis에 저장
		verificationCodeRepository.save(new VerificationCode(verificationCode.toString(), email));

	}

	//인증번호 확인 메소드
	//맞으면 그냥 동작 틀리면 에러
	public void compareVerificationKey(String verificationKey, String email) {

		System.out.println(verificationKey);
		System.out.println(email);

		if (!verificationCodeRepository.findById(verificationKey).isPresent())
			throw new IllegalArgumentException("올바른 인증번호가 아닙니다.");

		if (!email.equals(verificationCodeRepository.findById(verificationKey).get().getEmail()))
			throw new IllegalArgumentException("올바른경로가 아닙니다.");

	}

	//비밀번호 재설정
	public void resetPassword(UserPasswordResetRequest userPasswordResetRequest) {
		String email = userPasswordResetRequest.getEmail();
		String password = userPasswordResetRequest.getPassword();
		String verificationKey = userPasswordResetRequest.getVerificationKey();

		System.out.println(1);
		if (verificationCodeRepository.findById(verificationKey).isEmpty())
			throw new IllegalArgumentException("올바른 접근이 아닙니다.");

		if (!verificationCodeRepository.findById(verificationKey)
			.get()
			.getEmail()
			.equals(userRepository.findByEmail(email).get().getEmail()))
			throw new IllegalArgumentException("올바른 접근이 아닙니다.");

		//인증번호랑 이메일이 맞는 경우
		User user = userRepository.findByEmail(email).get();

		//이전 비밀번호랑 같으면 밴해주기
		if (passwordEncoder.matches(password, user.getPassword()))
			throw new IllegalArgumentException("이전 비밀번호와 다르게 설정해야합니다.");

		user.updatePassword(password);

		userRepository.save(user);
	}

	//user 개인정보 조회
	@Transactional
	public UserInfoResponsse getInfo(String token) {

		Long userId = jwtTokenProvider.getUserId(token);

		User user = userRepository.findMyDataById(userId);
		//이미지, 이름, 참고 url, introduce, skills
		return new UserInfoResponsse(user);
	}

	//경력인증 메소드
	public int careerValidation(UseApiRequest useApiRequest) throws
		UnsupportedEncodingException,
		JsonProcessingException,
		InterruptedException {
		EasyCodef codef = new EasyCodef();
		codef.setPublicKey(codefKey);
		codef.setClientInfoForDemo(codefId, codefSecret);
		
		HashMap<String, Object> parameterMap = new HashMap<String, Object>();

		parameterMap.put("organization", "0002"); //기관코드 002 고정
		parameterMap.put("isIdentityViewYN", "1"); //	주민번호 뒷자리 공개여부 1 고정
		parameterMap.put("loginType", "5"); //로그인 구분 5(간편로그인) 고정
		parameterMap.put("loginTypeLevel", "1"); //	간편인증 로그인 구분 (1:카카오톡, 2:페이코, 3:삼성패스, 4:KB모바일, 5:통신사(PASS), 6:네이버, 7:신한인증서, 8: toss)
		parameterMap.put("useType", "0");//가입자구분(용도구분), "0" : 전체, "1" : 직장가입자, "2" : 지역가입자, "3" : 가입자 전체 0 고정
		parameterMap.put("userName", useApiRequest.getUserName()); //이름}
		parameterMap.put("identity", useApiRequest.getIdentity()); //생년월일(8자리)
		parameterMap.put("phoneNo", useApiRequest.getPhoneNo()); // 핸드폰번호

		parameterMap.put("telecom", useApiRequest.getTelecom()); // 통신사 “0":SKT(SKT알뜰폰), “1”:KT(KT알뜰폰), “2":LG U+(LG U+알뜰폰)

		//1차 정보 요청
		String productUrl = "/v1/kr/public/pp/nhis-join/identify-confirmation";
		String firstData = codef.requestProduct(productUrl, EasyCodefServiceType.DEMO, parameterMap);

		//1차 결과물
		ObjectMapper objectMapper = new ObjectMapper();
		CareerApiResponse response = objectMapper.readValue(firstData, CareerApiResponse.class);

		// 이 과정을 메소드랑 레디스 이용해서 딜레이 줄이기
		try {
			// 1분 동안 대기
			TimeUnit.MINUTES.sleep(1);
		} catch (InterruptedException e) {
			// InterruptedException 처리
			e.printStackTrace();
		}

		parameterMap.put("simpleAuth", "1");
		parameterMap.put("is2Way", true);

		HashMap<String, Object> twoWayInfo = new HashMap<String, Object>();
		twoWayInfo.put("jobIndex", response.getData().getJobIndex());
		twoWayInfo.put("threadIndex", response.getData().getThreadIndex());
		twoWayInfo.put("jti", response.getData().getJti());
		twoWayInfo.put("twoWayTimestamp", System.currentTimeMillis() + (3 * 60 * 1000));
		parameterMap.put("twoWayInfo", twoWayInfo);

		//2차 정보 요청
		String secondData = codef.requestCertification(productUrl, EasyCodefServiceType.DEMO, parameterMap);

		objectMapper = new ObjectMapper();
		ApiDetailResponse apiDetailResponse = null;

		try {
			apiDetailResponse = objectMapper.readValue(secondData, ApiDetailResponse.class);
		} catch (Exception e) {
			e.printStackTrace(); // 역직렬화에 실패한 경우 예외처리 필요
		}

		if (apiDetailResponse == null)
			throw new IllegalArgumentException("정보를 조회할 수 없습니다.");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		long totalCareer = 0;

		for (ApiDetailResponse.Data d : apiDetailResponse.getData()) {
			if (d.getResJoinUserType().equals("직장피부양자"))
				continue;

			try {

				Date day1 = sdf.parse(d.getCommStartDate());
				Date day2 = sdf.parse(d.getCommEndDate());

				totalCareer += Math.abs(day1.getTime() - day2.getTime()) / (24 * 60 * 60 * 1000);

			} catch (ParseException e) {
				throw new IllegalArgumentException("정보를 처리할 수 없습니다.");
			}

		}

		int career = (int)totalCareer / 365;

		return career;
	}

	//user추가정보 입력
	@Transactional
	public void updateAdditionalInfo(AdditionalUserInfoRequest additionalUserInfoRequest) {

		if(additionalUserInfoRequest.getEmail().isEmpty()) throw new IllegalArgumentException("사용자의 정보가 불확실합니다.");
		if(userRepository.findByEmail(additionalUserInfoRequest.getEmail()).isEmpty()) throw new IllegalArgumentException("사용자의 정보가 불확실합니다.");

		User user = userRepository.findByEmail(additionalUserInfoRequest.getEmail()).get();

		if (!validAdditionalInfo(additionalUserInfoRequest))
			throw new IllegalArgumentException("잘못된 정보입니다.");

		UserImage userImage = additionalUserInfoRequest.getUserImage();
		userImageRepository.save(userImage);

		List<UserSkill> userSkills = additionalUserInfoRequest.getUserSkills();
		List<UserSkill> newSkills = new ArrayList<>();

		for (UserSkill s : userSkills) {

			UserSkill newSkill = UserSkill.builder()
				.user(user)
				.skill(skillRepository.findById(s.getSkill().getSkillId()).get())
				.skillLevel(s.getSkillLevel())
				.build();

			newSkills.add(newSkill);

			userSkillRepository.save(newSkill);
		}
		user.addUserInfo(userImage, newSkills, additionalUserInfoRequest);
		userRepository.save(user);

		System.out.println(user.getEmail());
		System.out.println(user.getCareer());
	}

	//user 정보 수정
	@Transactional
	public void updateInfo(String token, UserUpdateInfoRequest userUpdateInfoRequest) {
		Long userId = jwtTokenProvider.getUserId(token);
		User user = userRepository.findByUserId(userId).get();

		if (!validUpdateInfo(userUpdateInfoRequest))
			throw new IllegalArgumentException("정보를 찾을 수 없습니다.");

		UserImage userImage = userUpdateInfoRequest.getUserImage();
		userImageRepository.save(userImage);

		List<UserSkill> userSkills = userUpdateInfoRequest.getUserSkills();
		List<UserSkill> newSkills = new ArrayList<>();

		userSkillRepository.deleteByUser(user);

		for (UserSkill s : userSkills) {

			if(skillRepository.findById(s.getSkill().getSkillId()).isEmpty()) continue;

			UserSkill newSkill = UserSkill.builder()
				.user(user)
				.skill(skillRepository.findById(s.getSkill().getSkillId()).get())
				.skillLevel(s.getSkillLevel())
				.build();

			newSkills.add(newSkill);

			userSkillRepository.save(newSkill);
		}

		user.updateUser(userImage, newSkills, userUpdateInfoRequest);

		userRepository.save(user);
	}

	//로그아웃
	@Transactional
	public void logout(String refreshToken) {

		refreshTokenRepository.deleteById(refreshToken);

	}

	//회원탈퇴
	//토큰 기반으로 하는것이기 때문에 굳이 확인절차 필요?
	@Transactional
	public void deleteUser(String token) {

		long userId = jwtTokenProvider.getUserId(token);
		//엮인 테이블에 있는 정보도 모두 삭제 필요
		userRepository.deleteById(userId);
	}

	@Transactional
	public List<RankingDTO> getTopFive(){
		List<RankingDTO> rankers = userRepository.findOrderByRatingDesc(PageRequest.of(0, 5));
		System.out.println(rankers);
		return rankers;
	}

	@Transactional
	public List<Skill> getSkillList(){
		List<Skill> skills = skillRepository.findAll();

		return skills;
	}

	@Transactional
	public GetUserBeforeInfoDTO getUserBeforeInfo(String token){
		long userId = jwtTokenProvider.getUserId(token);
		User user = userRepository.findMyDataById(userId);

		return new GetUserBeforeInfoDTO(user);
	}

	//User 정보 수정할 때 검증할 메소드
	public boolean validUpdateInfo(UserUpdateInfoRequest userUpdateInfoRequest) {
		// if (userUpdateInfoRequest.getPassword() == null)
		// 	return false;
		if (userUpdateInfoRequest.getName() == null)
			return false;
		if (userUpdateInfoRequest.getBirth() == null)
			return false;
		if (userUpdateInfoRequest.getUserSkills() == null)
			return false;
		if (userUpdateInfoRequest.getReferenceUrl() == null)
			return false;
		if (userUpdateInfoRequest.getField() == null)
			return false;
		if (userUpdateInfoRequest.getUserImage() == null)
			return false;
		return true;
	}

	//User 정보 수정할 때 검증할 메소드
	public boolean validAdditionalInfo(AdditionalUserInfoRequest additionalUserInfoRequest) {
		if (additionalUserInfoRequest.getUserSkills() == null)
			return false;
		if (additionalUserInfoRequest.getReferenceUrl() == null)
			return false;
		if (additionalUserInfoRequest.getField() == null)
			return false;
		if (additionalUserInfoRequest.getUserImage() == null)
			return false;
		return true;
	}

}
