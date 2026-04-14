package backend.service;

import backend.dto.TopicResponse;
import backend.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {

	private final TopicRepository topicRepository;

	public TopicService(TopicRepository topicRepository) {
		this.topicRepository = topicRepository;
	}

	public List<TopicResponse> getAllTopics() {
		return topicRepository.findAll().stream()
			.map(topic -> new TopicResponse(topic.getTopicId(), topic.getName(), topic.getDescription()))
			.toList();
	}
}
