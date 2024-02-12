package com.link.back.security;

import com.link.back.dto.JwtToken;
import com.link.back.dto.RefreshToken;
import com.link.back.repository.RefreshTokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    private String tokenParser(String jwtToken) {
        if (jwtToken == null) return null;
        return jwtToken.charAt(6) == 32 ? jwtToken.substring(7) : jwtToken;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        // HttpServletRequest로 캐스팅
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // 헤더에서 JWT 를 받아옵니다. -> access토큰을 체크
        String accessToken = jwtTokenProvider.resolveAccessToken(httpRequest);

        System.out.println(accessToken);

        String refreshToken = "";
        //쿠키 가져오기
        Cookie[] cookies = httpRequest.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        accessToken = tokenParser(accessToken);
        refreshToken = tokenParser(refreshToken);
        // 유효한 토큰의 유무를 확인합니다.
        if (accessToken != null) {
            // 어세스 토큰이 존재하는 상황
            // 만료됐는지 확인
            if (jwtTokenProvider.validateToken(accessToken)) {
                this.setAuthentication(accessToken);
            }
            // 어세스 토큰이 만료된 상황 동시에 리프레시 토큰은 존재하는 상황
            else if (!jwtTokenProvider.validateToken(accessToken) && !(refreshToken.isEmpty())) {
                /// 리프레시 토큰 만료시간 검증
                boolean validateRefreshToken = jwtTokenProvider.validateToken(refreshToken);
                /// 리프레시 토큰 저장소 존재유무 확인
                boolean isRefreshToken = jwtTokenProvider.existsRefreshToken(refreshToken);
                //둘 다 만족할 때
                if (validateRefreshToken && isRefreshToken) {
                    /// 리프레시 토큰으로 userId 정보 가져오기
                    Long userId = jwtTokenProvider.getUserId(refreshToken);
                    JwtToken jwtToken = jwtTokenProvider.generateToken(userId);
                    // 응답 헤더에 어세스 토큰 추가
                    jwtTokenProvider.setHeaderAccessToken(httpResponse, jwtToken.getAccessToken());
                    // 컨텍스트에 넣기
                    this.setAuthentication(jwtToken.getAccessToken());
                }
            }
        }
        chain.doFilter(httpRequest, httpResponse);
    }

    // SecurityContext 에 Authentication 객체를 저장합니다.
    public void setAuthentication(String token) {
        // 토큰으로부터 유저 정보를 받아옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        // SecurityContext 에 Authentication 객체를 저장합니다.
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}

