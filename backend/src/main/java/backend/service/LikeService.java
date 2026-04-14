package backend.service;

import backend.dto.LikeResponse;
import backend.entity.LikeEntity;
import backend.entity.Post;
import backend.entity.User;
import backend.exception.ApiException;
import backend.repository.LikeRepository;
import backend.repository.PostRepository;
import backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

	private final LikeRepository likeRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	public LikeService(LikeRepository likeRepository, PostRepository postRepository, UserRepository userRepository) {
		this.likeRepository = likeRepository;
		this.postRepository = postRepository;
		this.userRepository = userRepository;
	}

	public LikeResponse toggleLike(Integer postId, Integer userId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Post not found"));
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

		return likeRepository.findByPostAndUser(post, user)
			.map(existingLike -> {
				likeRepository.delete(existingLike);
				post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
				postRepository.save(post);
				return new LikeResponse(post.getPostId(), post.getLikeCount(), false);
			})
			.orElseGet(() -> {
				LikeEntity like = new LikeEntity();
				like.setPost(post);
				like.setUser(user);
				likeRepository.save(like);
				post.setLikeCount(post.getLikeCount() + 1);
				postRepository.save(post);
				return new LikeResponse(post.getPostId(), post.getLikeCount(), true);
			});
	}
}
