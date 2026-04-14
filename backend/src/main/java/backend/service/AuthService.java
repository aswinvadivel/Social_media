package backend.service;

import backend.dto.CurrentUserResponse;
import backend.dto.LoginRequest;
import backend.dto.LoginResponseData;
import backend.entity.User;
import backend.exception.ApiException;
import backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.tokenService = tokenService;
	}

	public LoginResponseData login(LoginRequest request) {
		User user = userRepository.findByUsername(request.username())
			.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}

		String token = tokenService.issueToken(user.getUserId());
		return new LoginResponseData(user.getUserId(), user.getUsername(), user.getEmail(), token);
	}

	public CurrentUserResponse getCurrentUser(Integer userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
		return new CurrentUserResponse(user.getUserId(), user.getUsername(), user.getEmail(), user.getBio());
	}
}
