package backend.repository;

import backend.entity.LikeEntity;
import backend.entity.Post;
import backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity, Integer> {
	Optional<LikeEntity> findByPostAndUser(Post post, User user);
}
