package backend.controller;

import backend.dto.ApiResponse;
import backend.dto.CreatePostRequest;
import backend.dto.LikeResponse;
import backend.dto.Pagination;
import backend.dto.PostResponse;
import backend.service.LikeService;
import backend.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

	private final PostService postService;
	private final LikeService likeService;

	public PostController(PostService postService, LikeService likeService) {
		this.postService = postService;
		this.likeService = likeService;
	}

	@GetMapping
	public ApiResponse<List<PostResponse>> getPosts(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int limit,
		@RequestParam(required = false) Integer topicId
	) {
		Page<PostResponse> posts = postService.getPosts(page, limit, topicId);
		Pagination pagination = postService.toPagination(posts, page, limit);
		return ApiResponse.success(posts.getContent(), pagination);
	}

	@GetMapping("/{postId}")
	public ApiResponse<PostResponse> getPost(@PathVariable Integer postId) {
		return ApiResponse.success(postService.getPostById(postId));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<PostResponse>> createPost(
		@Valid @RequestBody CreatePostRequest request,
		HttpServletRequest servletRequest
	) {
		Integer userId = (Integer) servletRequest.getAttribute("authenticatedUserId");
		PostResponse created = postService.createPost(userId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
	}

	@PostMapping("/{postId}/like")
	public ApiResponse<LikeResponse> toggleLike(@PathVariable Integer postId, HttpServletRequest servletRequest) {
		Integer userId = (Integer) servletRequest.getAttribute("authenticatedUserId");
		return ApiResponse.success(likeService.toggleLike(postId, userId));
	}
}
