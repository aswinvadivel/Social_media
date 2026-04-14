package backend.service;

import backend.dto.CreatePostRequest;
import backend.dto.Pagination;
import backend.dto.PostResponse;
import backend.entity.Post;
import backend.entity.Topic;
import backend.entity.User;
import backend.exception.ApiException;
import backend.repository.PostRepository;
import backend.repository.TopicRepository;
import backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PostService {

	private final PostRepository postRepository;
	private final TopicRepository topicRepository;
	private final UserRepository userRepository;

	public PostService(PostRepository postRepository, TopicRepository topicRepository, UserRepository userRepository) {
		this.postRepository = postRepository;
		this.topicRepository = topicRepository;
		this.userRepository = userRepository;
	}

	public Page<PostResponse> getPosts(int page, int limit, Integer topicId) {
		int safePage = Math.max(1, page);
		int safeLimit = Math.max(1, limit);
		Pageable pageable = PageRequest.of(safePage - 1, safeLimit);
		Page<Post> result = topicId == null
			? postRepository.findAllByOrderByCreatedAtDesc(pageable)
			: postRepository.findByTopicTopicIdOrderByCreatedAtDesc(topicId, pageable);

		return result.map(this::toResponse);
	}

	public Pagination toPagination(Page<?> pageData, int page, int limit) {
		return new Pagination(page, limit, pageData.getTotalElements(), pageData.getTotalPages());
	}

	public PostResponse getPostById(Integer postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Post not found"));
		return toResponse(post);
	}

	public PostResponse createPost(Integer userId, CreatePostRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
		Topic topic = topicRepository.findById(request.topicId())
			.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid topicId"));

		Post post = new Post();
		post.setUser(user);
		post.setTopic(topic);
		post.setTitle(request.title().trim());
		post.setContent(request.content().trim());
		post.setLikeCount(0);
		post.setCommentCount(0);

		Post saved = postRepository.save(post);
		return toResponse(saved);
	}

	private PostResponse toResponse(Post post) {
		return new PostResponse(
			post.getPostId(),
			post.getUser().getUserId(),
			post.getUser().getUsername(),
			post.getTopic().getTopicId(),
			post.getTopic().getName(),
			post.getTitle(),
			post.getContent(),
			post.getLikeCount(),
			post.getCommentCount(),
			post.getCreatedAt()
		);
	}
}
