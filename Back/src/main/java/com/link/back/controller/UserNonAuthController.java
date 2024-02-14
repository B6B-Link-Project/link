package com.link.back.controller;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.link.back.dto.Image;
import com.link.back.dto.JwtToken;
import com.link.back.dto.LoginRequest;
import com.link.back.dto.RankingDTO;
import com.link.back.dto.RefreshToken;
import com.link.back.dto.UserSignUpDto;
import com.link.back.dto.request.AdditionalUserInfoRequest;
import com.link.back.dto.request.SendEmailRequest;
import com.link.back.dto.request.UseApiRequest;
import com.link.back.dto.request.UserFindEmailRequest;
import com.link.back.dto.request.UserPasswordResetRequest;
import com.link.back.dto.request.VerificationRequest;
import com.link.back.entity.Skill;
import com.link.back.repository.RefreshTokenRepository;
import com.link.back.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserNonAuthController {

    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${REFRESH_TOKEN_EXPIRE_TIME}")
    private long refreshTokenExpireTime;

    public UserNonAuthController(UserService userService, RefreshTokenRepository refreshTokenRepository ) {
        this.userService = userService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    //회원 가입
    //커리어 정보 추가
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody UserSignUpDto userSignUpDto) throws Exception {

        //이메일 보내기
        //이 과정에서 오류나면 잘못된 이메일이라고 오류 보내기
        // userService.sendVerificationSignUpEmail(userSignUpDto.getEmail());


        // userService.sendVerificationSignUpEmail(userSignUpDto);

        //이메일 보낸거 확인
        String response = userService.signup(userSignUpDto);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //회원가입 이메일 인증 - 메일 발송
    // @PostMapping("/signup/email")
    // public ResponseEntity<String> verifyingEmail(@Valid @RequestBody SendEmailRequest SendEmailRequest){
    //
    //     userService.sendVerificationSignUpEmail(SendEmailRequest.email());
    //
    //     return new ResponseEntity<>("이메일을 발송했습니다.", HttpStatus.OK);
    // }

    //회원가입 이메일 인증 - 확인
    @GetMapping("/signup/email/verification")
    public ResponseEntity<String> confirmEmailVerification(@Valid @RequestParam String verificationCode, @RequestParam String email){

        userService.compareVerificationKey(verificationCode, email);

        return new ResponseEntity<>("이메일 인증이 완료되었습니다.", HttpStatus.ACCEPTED);
    }

    //경력인증
    @PostMapping("career")
    public ResponseEntity<Integer> validCareer(@Valid @RequestBody UseApiRequest useApiRequest) throws
        UnsupportedEncodingException,
        JsonProcessingException,
        InterruptedException {

        int result = userService.careerValidation(useApiRequest);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@Valid @RequestBody LoginRequest loginRequest){

        JwtToken jwtToken = userService.login(loginRequest);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();

        // AUTHORIZATION에 Access 토큰을 넣음
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken.getAccessToken());

        // ResponseCookie 객체를 생성하고, 쿠키에 Refresh 토큰을 설정
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", jwtToken.getRefreshToken())
                .maxAge(refreshTokenExpireTime)
                .httpOnly(true)
//                .sameSite("None") // SameSite 속성을 제3자 쿠키에 대해 None으로 설정
//                .secure(true) // 필수로 같이 설정해줘야함
                .path("/")
                .build();

        // refresh 토큰을 넣은 쿠키를 헤더에 담아서 보냄
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // refresh 토큰을 redis에 넣어줌
        RefreshToken refreshToken = new RefreshToken(jwtToken.getRefreshToken());
        refreshTokenRepository.save(refreshToken);

        // ResponseEntity를 생성하고, 헤더와 상태 코드를 설정
        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }

    //oauth2 로그인 성공시
    //refresh 컴포넌트에서 메인으로 넘어가면서 요청
    @PostMapping("/oauth2/access")
    public ResponseEntity<String> getAccessToken(@CookieValue(value = "refreshToken", defaultValue = "") String refreshToken){

        if(refreshToken.isEmpty()) throw new IllegalArgumentException("쿠키가 제대로 설정되지 않았습니다.");

        String accessToken = userService.oauth2Token(refreshToken);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();

        // AUTHORIZATION에 Access 토큰을 넣음
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        return ResponseEntity.ok()
            .headers(headers)
            .build();
    }

    //이메일 찾기
    @PostMapping("/email")
    public ResponseEntity<String> findEmail (@Valid @RequestBody UserFindEmailRequest userFindEmailRequest) throws Exception {

        System.out.println(userFindEmailRequest.getBirth());

        String name = userFindEmailRequest.getName();
        LocalDate birth = userFindEmailRequest.getBirth();
        String phoneNumber = userFindEmailRequest.getPhoneNumber();

        String email = userService.findEmail(name, birth, phoneNumber);

        //로그인 페이지로 보내주기
        if(email.equals("NotFound")){
            return new ResponseEntity<>("아이디가 존재하지 않습니다", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("아이디는" + email +  "입니다", HttpStatus.OK);
    }

    //비밀번호 찾기 메일인증요청
    //이걸 password/verification
    @PostMapping("/email/verification")
    public ResponseEntity<String> requestVerification(@Valid @RequestBody SendEmailRequest sendEmailRequest){

        String email = sendEmailRequest.email();

        userService.sendVerificationEmail(email);

        return new ResponseEntity<>("메일을 확인하세요", HttpStatus.OK);

    }

    //비밀번호 찾기 - 메일인증확인
    //요걸 password/verification/confirm 요렇게 바꿀까 생각중
    @PostMapping("/password/verification")
    public ResponseEntity<Boolean> comparedVerification(@Valid @RequestBody VerificationRequest verificationRequest){

        String verificationKey = verificationRequest.verificationKey();
        String email = verificationRequest.email();

        userService.compareVerificationKey(verificationKey, email);

        //에러 안나면 항상 True
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);

    }

    //비밀번호 찾기 - 비밀번호 변경
    //로직 바꾸기
    @PostMapping("/password")
    public ResponseEntity<String> changePassword (@Valid @RequestBody UserPasswordResetRequest userPasswordResetRequest) throws Exception {

        userService.resetPassword(userPasswordResetRequest);

        //로그인 페이지로 보내주기
        return new ResponseEntity<>("비밀번호 변경 완료", HttpStatus.OK);
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(name = "refreshToken") String token) {

        userService.logout(token);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", token)
            .maxAge(0)
            .httpOnly(true)
            .path("/")
            .build();

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok()
            .headers(headers)
            .build();
    }

    //최상의 rating user 5명의 정보를 출력
    @GetMapping("/ranking")
    public ResponseEntity<List<RankingDTO>> getRanker(){
        List<RankingDTO> rankers = userService.getTopFive();

        return new ResponseEntity<>(rankers, HttpStatus.OK);
    }

    //추가정보 입력(email을 넘겨받아서 할거임 그래서 열로 와야함)
    //회원 추가 정보 입력
    @PostMapping("/addtionalinfo")
    public ResponseEntity<String> addInfo(@Valid @RequestBody AdditionalUserInfoRequest additionalUserInfoRequest){

        userService.updateAdditionalInfo(additionalUserInfoRequest);

        return new ResponseEntity<>("추가정보입력이 완료되었습니다.", HttpStatus.OK);
    }

    //추가정보, 정보 수정시에 스킬 리스트 불러오는 메소드
    @GetMapping("/skillList")
    public ResponseEntity<List<Skill>> getSkills(){

        List<Skill> skills = userService.getSkillList();

        return new ResponseEntity<List<Skill>>(skills, HttpStatus.OK);
    }

    @PostMapping("/image")
    public ResponseEntity<Image> transformImage(@RequestPart(value = "image", required = false) MultipartFile image){

        Image img = null;

        img = userService.createImage(image);

        return new ResponseEntity<>(img, HttpStatus.CREATED);
    }
}
