


package com.genius.gitget.topic.service;

import com.genius.gitget.instance.domain.Instance;
import com.genius.gitget.instance.repository.InstanceRepository;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;
    private final InstanceRepository instanceRepository;


    // 토픽 리스트 요청
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    // 토픽 상세정보 요청
    public Topic getTopicById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 토픽을 찾을 수 없습니다."));
    }

    // 토픽 생성 요청
    public Topic createTopic(Topic topic) {
        return topicRepository.save(topic);
    }

    // 토픽 수정 요청
    /*
     * 인스턴스가 존재한다면 img, description, 유의사항만 수정가능
     * 존재하지 않는다면 모든요소 수정가능
     * 수정 버튼 클릭 -> 프론트에서 해당 토픽 정보를 제공 ->
     * 백엔드에서 해당 토픽이 인스턴스를 존재유무 파악 후 DTO 제공 -> 프론트에서 받아서 판단 후 수정불가능한 필드 막음
     * */
    public Topic updateTopic(Long topicId, Topic topicDetails) {
        Topic topic = getTopicById(topicId);
        boolean hasInstance = !topic.getInstanceList().isEmpty();
        if (hasInstance) {
            topic.hasInstanceUpdate(topicDetails.getDescription());
        } else {
            topic.hasNotInstanceUpdate(topicDetails.getTitle(), topicDetails.getDescription(),
                    topicDetails.getTags(), topicDetails.getPoint_per_person());
        }
        return topicRepository.save(topic);
    }

    // 토픽 삭제 요청
    public void deleteTopic(Long topicId) {
        Topic topic = getTopicById(topicId);
        topicRepository.delete(topic);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    private class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}
