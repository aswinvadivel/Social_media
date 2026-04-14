package backend.dto;

public record LikeResponse(
	Integer postId,
	Integer likeCount,
	boolean liked
) {
}
