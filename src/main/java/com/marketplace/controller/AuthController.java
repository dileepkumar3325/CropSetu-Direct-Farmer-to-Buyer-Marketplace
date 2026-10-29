package com.marketplace.controller;

import com.marketplace.entity.UserAccount;
import com.marketplace.repository.UserAccountRepository;
import com.marketplace.security.JwtService;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final UserAccountRepository users;
	private final PasswordEncoder encoder;
	private final JwtService jwt;

	public AuthController(UserAccountRepository users, PasswordEncoder encoder, JwtService jwt) {
		this.users = users;
		this.encoder = encoder;
		this.jwt = jwt;
	}

	public static class RegisterRequest {
		@NotBlank
		public String name;
		@NotBlank
		@Email
		public String email;
		@NotBlank
		@Size(min = 8, max = 72)
		public String password;
		public String role;
	}

	public static class LoginRequest {
		@NotBlank
		@Email
		public String email;
		@NotBlank
		public String password;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest r) {
		String email = r.email.trim().toLowerCase(Locale.ROOT);
		if (users.existsByEmailIgnoreCase(email))
			return ResponseEntity.status(HttpStatus.CONFLICT).body(error("An account with this email already exists."));
		String role = "BUYER";
		if ("FARMER".equalsIgnoreCase(r.role))
			role = "FARMER";
		else if (r.role != null && !r.role.trim().isEmpty() && !"BUYER".equalsIgnoreCase(r.role))
			return ResponseEntity.badRequest()
					.body(error("Choose Farmer or Buyer. Admin accounts cannot be registered publicly."));
		UserAccount u = new UserAccount();
		u.setName(r.name.trim());
		u.setEmail(email);
		u.setPasswordHash(encoder.encode(r.password));
		u.setRole(role);
		users.save(u);
		return ResponseEntity.status(HttpStatus.CREATED).body(authResponse(u));
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest r) {
		Optional<UserAccount> found = users.findByEmailIgnoreCase(r.email.trim());
		if (!found.isPresent() || !encoder.matches(r.password, found.get().getPasswordHash()))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("Invalid email or password."));
		return ResponseEntity.ok(authResponse(found.get()));
	}

	@GetMapping("/me")
	public ResponseEntity<?> me(org.springframework.security.core.Authentication authentication) {
		Optional<UserAccount> found = users.findByEmailIgnoreCase(authentication.getName());
		return found.isPresent() ? ResponseEntity.ok(publicUser(found.get()))
				: ResponseEntity.status(404).body(error("User not found."));
	}

	private Map<String, Object> authResponse(UserAccount u) {
		Map<String, Object> m = publicUser(u);
		m.put("token", jwt.generateToken(u.getEmail(), u.getRole()));
		m.put("tokenType", "Bearer");
		m.put("expiresIn", 86400);
		return m;
	}

	private Map<String, Object> publicUser(UserAccount u) {
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		m.put("id", u.getId());
		m.put("name", u.getName());
		m.put("email", u.getEmail());
		m.put("role", u.getRole());
		return m;
	}

	private Map<String, String> error(String message) {
		Map<String, String> m = new HashMap<String, String>();
		m.put("message", message);
		return m;
	}
}
