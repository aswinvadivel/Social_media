package backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
	@NotNull(message = "topicId is required") Integer topicId,
	@NotBlank(message = "title is required")
	@Size(max = 200, message = "title must be at most 200 characters")
	String title,
	@NotBlank(message = "content is required")
	String content
) {
}
