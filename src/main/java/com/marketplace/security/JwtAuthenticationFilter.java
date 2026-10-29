package com.marketplace.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")
				&& SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				Claims claims = jwtService.parse(header.substring(7));
				String email = claims.getSubject();
				String role = claims.get("role", String.class);
				if (email != null && role != null) {
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null,
							Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			} catch (JwtException | IllegalArgumentException ex) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json");
				response.getWriter().write("{\"message\":\"Invalid or expired access token. Please log in again.\"}");
				return;
			}
		}
		chain.doFilter(request, response);
	}
}
