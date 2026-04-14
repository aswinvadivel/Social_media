package backend.dto;

public record LoginResponseData(
	Integer userId,
	String username,
	String email,
	String token
) {
}
