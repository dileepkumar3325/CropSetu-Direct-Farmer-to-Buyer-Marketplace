package com.marketplace.config;

import com.marketplace.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	private final JwtAuthenticationFilter jwtFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
		this.jwtFilter = jwtFilter;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable().cors().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and().authorizeRequests()
				.antMatchers("/api/auth/register", "/api/auth/login", "/swagger-ui.html", "/swagger-ui/**",
						"/v3/api-docs/**")
				.permitAll().antMatchers(HttpMethod.GET, "/api/crops", "/api/crops/**").permitAll()
				.antMatchers(HttpMethod.POST, "/api/crops/**", "/api/crops").hasAnyRole("FARMER", "ADMIN")
				.antMatchers(HttpMethod.PUT, "/api/crops/**").hasAnyRole("FARMER", "ADMIN")
				.antMatchers(HttpMethod.DELETE, "/api/crops/**").hasAnyRole("FARMER", "ADMIN")
				.antMatchers(HttpMethod.POST, "/api/orders").hasRole("BUYER").antMatchers(HttpMethod.GET, "/api/orders")
				.hasRole("BUYER").antMatchers(HttpMethod.PUT, "/api/orders/*/status").hasRole("ADMIN")
				.antMatchers("/api/orders/**").authenticated().anyRequest().authenticated();
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
