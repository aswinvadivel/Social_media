package backend.controller;

import backend.dto.ApiResponse;
import backend.dto.CurrentUserResponse;
import backend.dto.LoginRequest;
import backend.dto.LoginResponseData;
import backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public ApiResponse<LoginResponseData> login(@Valid @RequestBody LoginRequest request) {
		return ApiResponse.success(authService.login(request));
	}

	@GetMapping("/me")
	public CurrentUserResponse me(HttpServletRequest request) {
		Integer userId = (Integer) request.getAttribute("authenticatedUserId");
		return authService.getCurrentUser(userId);
	}
}
