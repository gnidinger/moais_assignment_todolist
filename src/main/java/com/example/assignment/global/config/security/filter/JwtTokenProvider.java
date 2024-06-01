package com.example.assignment.global.config.security.filter;

import java.util.Date;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final UserDetailsService userDetailsService;

	@Value("${jwt.token.secret-key}")
	private String secretKey;

	@Value("${jwt.token.expiration-minutes}")
	private long expireTime;

	// JWT 토큰을 파싱하기 위한 SecretKey 생성
	private SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public String createToken(String userId) {
		Claims claims = Jwts.claims().setSubject(userId);
		long expireTimeInMillis = expireTime * 60 * 1000; // 분을 밀리초로 변환
		Date now = new Date();
		Date validity = new Date(now.getTime() + expireTimeInMillis);

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(validity)
			.signWith(getSecretKey(), SignatureAlgorithm.HS512)
			.compact();
	}

	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(getSecretKey()) // SecretKey 사용
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public Authentication getAuthentication(String token) {
		String userId = getUserId(token);
		UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public String getUserId(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(getSecretKey())
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
	}
}
