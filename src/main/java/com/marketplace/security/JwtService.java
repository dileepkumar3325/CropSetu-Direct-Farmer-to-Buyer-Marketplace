package com.marketplace.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
	private final SecretKey key;
	private final long expirationMs;

	public JwtService(@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
		byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
		if (bytes.length < 32)
			throw new IllegalArgumentException("app.jwt.secret must be at least 32 bytes");
		this.key = Keys.hmacShaKeyFor(bytes);
		this.expirationMs = expirationMs;
	}

	public String generateToken(String email, String role) {
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("role", role.toUpperCase());
		Date now = new Date();
		return Jwts.builder().setClaims(claims).setSubject(email).setIssuedAt(now)
				.setExpiration(new Date(now.getTime() + expirationMs)).signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	public Claims parse(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}
}
