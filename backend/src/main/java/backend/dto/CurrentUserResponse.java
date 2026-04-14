package backend.dto;

public record CurrentUserResponse(
	Integer userId,
	String username,
	String email,
	String bio
) {
}
