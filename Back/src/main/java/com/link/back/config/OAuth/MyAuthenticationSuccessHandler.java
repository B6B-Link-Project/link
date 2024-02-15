package com.link.back.config.OAuth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.link.back.dto.JwtToken;
import com.link.back.dto.RefreshToken;
import com.link.back.repository.RefreshTokenRepository;
import com.link.back.repository.UserRepository;
import com.link.back.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenProvider jwtTokenProvider;

    @Value("${REFRESH_TOKEN_EXPIRE_TIME}")
    private long refreshTokenExpireTime;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, SecurityException {

		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		String email = oAuth2User.getAttribute("email");

		//어디 플랫폼인지 가져오는건데 필요한지 의문임
		// String provider = oAuth2User.getAttribute("provider");

		//존재하는 User인지 확인
		boolean isExist = oAuth2User.getAttribute("exist");
		//존재하는 User인 경우
		if (isExist) {
			Long userId = userRepository.findByEmail(email).get().getUserId();
			//회원이 존재할 때 토큰 발급, 리프레쉬 토큰 추가로 넣어야함
			JwtToken token = jwtTokenProvider.generateToken(userId);

			//로그인 성공페이지 url 확인해서 받아야함
			String targetUrl = UriComponentsBuilder.fromUriString("https://i10a602.p.ssafy.io/refresh")
				.build()
				//이 부분은 더 확인해봐야함
				.encode(StandardCharsets.UTF_8)
				.toUriString();

			ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
				.maxAge(refreshTokenExpireTime)
				.secure(true)
				.httpOnly(true)
				.path("/")
				.build();

            //프론트로 헤더에 accessToken, 쿠키에 refreshToken 을 들고 감
			response.setHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            //리프레시 토큰 레디스에 저장 -> 비교목적
            refreshTokenRepository.save(new RefreshToken(token.getRefreshToken()));

			getRedirectStrategy().sendRedirect(request, response, targetUrl);

		} else { //존재하지 않는 user인 경우
            String targetUrl = UriComponentsBuilder.fromUriString("https://i10a602.p.ssafy.io/register")
				.queryParam("email", email)
				.build()
                //이 부분은 더 확인해봐야함
                .encode(StandardCharsets.UTF_8)
                .toUriString();

			getRedirectStrategy().sendRedirect(request, response, targetUrl);

        }
	}
}
