package backend.config;

import backend.entity.Post;
import backend.entity.Topic;
import backend.entity.User;
import backend.repository.PostRepository;
import backend.repository.TopicRepository;
import backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

	private final UserRepository userRepository;
	private final TopicRepository topicRepository;
	private final PostRepository postRepository;
	private final PasswordEncoder passwordEncoder;

	public DataSeeder(
		UserRepository userRepository,
		TopicRepository topicRepository,
		PostRepository postRepository,
		PasswordEncoder passwordEncoder
	) {
		this.userRepository = userRepository;
		this.topicRepository = topicRepository;
		this.postRepository = postRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		if (userRepository.count() == 0) {
			seedUsers();
		}
		if (topicRepository.count() == 0) {
			seedTopics();
		}
		if (postRepository.count() == 0) {
			seedPosts();
		}
	}

	private void seedUsers() {
		List<User> users = List.of(
			createUser("john_doe", "john@example.com", "Tech enthusiast and coder"),
			createUser("jane_smith", "jane@example.com", "Designer and creative thinker"),
			createUser("alex_tech", "alex@example.com", "Full-stack developer"),
			createUser("emma_writer", "emma@example.com", "Content creator"),
			createUser("mike_gamer", "mike@example.com", "Gaming enthusiast")
		);
		userRepository.saveAll(users);
	}

	private User createUser(String username, String email, String bio) {
		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setBio(bio);
		user.setPasswordHash(passwordEncoder.encode("password123"));
		return user;
	}

	private void seedTopics() {
		List<Topic> topics = List.of(
			createTopic("Technology", "Discuss the latest tech trends and innovations"),
			createTopic("Gaming", "All things gaming - reviews, streams, discussions"),
			createTopic("Design", "UI/UX design, graphic design, and creative work"),
			createTopic("Lifestyle", "Health, fitness, personal development"),
			createTopic("Entertainment", "Movies, TV shows, music, and pop culture")
		);
		topicRepository.saveAll(topics);
	}

	private Topic createTopic(String name, String description) {
		Topic topic = new Topic();
		topic.setName(name);
		topic.setDescription(description);
		return topic;
	}

	private void seedPosts() {
		List<User> users = userRepository.findAll();
		List<Topic> topics = topicRepository.findAll();
		if (users.size() < 5 || topics.size() < 5) {
			return;
		}

		List<Post> posts = List.of(
			createPost(users.get(0), topics.get(0), "React 19 is Game Changing", "Just started using React 19 and I'm blown away! #ReactJS #Frontend #WebDevelopment The new features like automatic batching really improve performance.", 42, 5),
			createPost(users.get(1), topics.get(2), "Minimalist UI Design Trends 2026", "Minimalism continues to dominate UI/UX design. #Design #UI #Minimalism Check out my latest portfolio piece with clean, modern aesthetics.", 35, 4),
			createPost(users.get(2), topics.get(0), "Spring Boot Best Practices", "Here are my top 10 Spring Boot best practices for production apps #SpringBoot #Backend #Java #Development Always use proper logging and error handling!", 28, 3),
			createPost(users.get(0), topics.get(1), "Elden Ring DLC Review", "The new Elden Ring DLC is absolutely incredible #Gaming #EldenRing #Review The boss fights are challenging but fair. Highly recommended!", 19, 2),
			createPost(users.get(3), topics.get(4), "Best Netflix Shows of 2026", "My top 5 Netflix shows this year #Entertainment #Netflix #Shows Don't sleep on the new sci-fi series!", 23, 6),
			createPost(users.get(4), topics.get(1), "Gaming PC Build Guide 2026", "How to build the ultimate gaming PC #Gaming #Hardware #PCBuilder Update your GPU and CPU for 4K gaming", 17, 1),
			createPost(users.get(1), topics.get(2), "Color Psychology in Design", "Understanding how colors impact user experience #Design #ColorTheory #UX The psychology behind color choices is fascinating!", 14, 2),
			createPost(users.get(2), topics.get(0), "MySQL Performance Optimization", "Tips to optimize your MySQL queries #Database #MySQL #Performance #Backend Always use proper indexing! #SQL", 31, 3)
		);

		postRepository.saveAll(posts);
	}

	private Post createPost(User user, Topic topic, String title, String content, int likeCount, int commentCount) {
		Post post = new Post();
		post.setUser(user);
		post.setTopic(topic);
		post.setTitle(title);
		post.setContent(content);
		post.setLikeCount(likeCount);
		post.setCommentCount(commentCount);
		return post;
	}
}
