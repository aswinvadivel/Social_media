package backend.controller;

import backend.dto.ApiResponse;
import backend.dto.TopicResponse;
import backend.service.TopicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/topics")
public class TopicController {

	private final TopicService topicService;

	public TopicController(TopicService topicService) {
		this.topicService = topicService;
	}

	@GetMapping
	public ApiResponse<List<TopicResponse>> getAllTopics() {
		return ApiResponse.success(topicService.getAllTopics());
	}
}
