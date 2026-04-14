package backend.dto;

public record Pagination(
	int page,
	int limit,
	long total,
	int totalPages
) {
}
