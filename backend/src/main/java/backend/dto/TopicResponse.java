package backend.dto;

public record TopicResponse(
	Integer topicId,
	String name,
	String description
) {
}
