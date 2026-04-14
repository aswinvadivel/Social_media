package backend.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

	private final Map<String, Integer> tokenStore = new ConcurrentHashMap<>();

	public String issueToken(Integer userId) {
		String token = UUID.randomUUID().toString();
		tokenStore.put(token, userId);
		return token;
	}

	public Optional<Integer> resolveUserId(String token) {
		return Optional.ofNullable(tokenStore.get(token));
	}
}
