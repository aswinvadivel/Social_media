package backend.dto;

import java.time.Instant;

public record PostResponse(
	Integer postId,
	Integer userId,
	String username,
	Integer topicId,
	String topicName,
	String title,
	String content,
	Integer likeCount,
	Integer commentCount,
	Instant createdAt
) {
}
