package com.link.back.security;

import com.link.back.dto.JwtToken;
import com.link.back.dto.RefreshToken;
import com.link.back.exception.AuthorizationException;
import com.link.back.repository.RefreshTokenRepository;
import com.link.back.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@Getter
public class JwtTokenProvider {
    private final Key key;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;

    // application.properties에서 secret 값 가져와서 key에 저장
    @Value("${ACCESS_TOKEN_EXPIRE_TIME}")
    private long accessTokenExpireTime;

    @Value("${REFRESH_TOKEN_EXPIRE_TIME}")
    private long refreshTokenExpireTime;

    public JwtTokenProvider(@Value("${JWT_SECRET_KEY}") String secretKey, RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, CustomUserDetailsService userDetailsService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    // User 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public JwtToken generateToken(Long userId) {
        long now = System.currentTimeMillis();
        long accessTokenExpiresInMillis = now + accessTokenExpireTime;// 30 minutes in milliseconds
        Date accessTokenExpiresIn = new Date(accessTokenExpiresInMillis);

        // Access Token 생성
        // userId를 숫자 그대로 담을수 없음 그래서 Stirng으로 변환하고 받음
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(now + refreshTokenExpireTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();


        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        UserDetails userDetails = userDetailsService.loadUserByUserId(this.getUserId(accessToken));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Long getUserId(String token) {
        if (token.substring(0,6).equals("Bearer")) {
            token = token.substring(7);
        }

        Long userId =  Long.parseLong(Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject());
        return userId;
    }

    // Request의 Header에서 AccessToken 값을 가져옵니다. "authorization" : "token'
    public String resolveAccessToken(HttpServletRequest request) {
        if(request.getHeader("authorization") != null )
            return request.getHeader("authorization").substring(7);
        //토큰 에러로 나중에 바꿀 예정
        return null;
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            log.info(e.getMessage());
            return false;
        }
    }

    // 어세스 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("Authorization", "bearer "+ accessToken);
    }

    // RefreshToken 존재유무 확인
    public boolean existsRefreshToken(String refreshToken) {

        Optional<RefreshToken> token = refreshTokenRepository.findById(refreshToken);

        if(token != null) return true;

        return false;

    }

    //refreshToken을 받아서 accessToken을 발급해주는 메소드
    public String generateOauth2token(String refreshToken){

        Date accessTokenExpiresIn = new Date(System.currentTimeMillis() + accessTokenExpireTime);

        long userId = getUserId(refreshToken);

        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));

        String accessToken = Jwts.builder()
            .setClaims(claims)
            .setExpiration(accessTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        return accessToken;

    }



}