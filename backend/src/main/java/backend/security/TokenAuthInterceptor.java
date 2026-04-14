package backend.security;

import backend.exception.ApiException;
import backend.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenAuthInterceptor implements HandlerInterceptor {

	private final TokenService tokenService;

	public TokenAuthInterceptor(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (HttpMethod.OPTIONS.matches(request.getMethod())) {
			return true;
		}

		String path = request.getRequestURI().replaceFirst(request.getContextPath(), "");
		boolean needsAuth = path.equals("/auth/me")
			|| (path.equals("/posts") && HttpMethod.POST.matches(request.getMethod()))
			|| (path.matches("/posts/\\d+/like") && HttpMethod.POST.matches(request.getMethod()));

		if (!needsAuth) {
			return true;
		}

		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}

		String token = authHeader.substring(7);
		Integer userId = tokenService.resolveUserId(token)
			.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
		request.setAttribute("authenticatedUserId", userId);
		return true;
	}
}
