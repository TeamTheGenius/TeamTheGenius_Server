package com.genius.gitget.topic.service;

import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.dto.TopicDTO;
import com.genius.gitget.topic.repository.TopicRepository;
import com.genius.gitget.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;

    // 토픽 리스트 요청
    public Page<Topic> getAllTopics(Pageable pageable) {
        return topicRepository.findByIdOrderByIdDesc(pageable);
    }

    // 토픽 상세정보 요청
    public Topic getTopicById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new BusinessException("해당 토픽을 찾을 수 없습니다."));
    }

    // 토픽 생성 요청
    public Topic createTopic(TopicDTO topicDTO) {
        Topic topic = Topic.builder()
                .title(topicDTO.title())
                .description(topicDTO.description())
                .tags(topicDTO.tags())
                .point_per_person(topicDTO.point_per_person())
                .build();

        return topicRepository.save(topic);
    }

    // 토픽 수정 요청
    /*
     * 인스턴스가 존재한다면 img, description, 유의사항만 수정가능
     * 존재하지 않는다면 모든요소 수정가능
     * 프론트엔드에서 수정 버튼 클릭: 사용자가 UI에서 특정 토픽의 수정 버튼을 클릭합니다.
     * 프론트엔드에서 백엔드에 요청: 프론트엔드는 백엔드에 해당 토픽의 상세 정보를 요청합니다. 이 요청은 토픽의 인스턴스 존재 유무에 대한 정보도 포함할 수 있습니다.
     * 백엔드에서 토픽 인스턴스 존재 유무 파악: 백엔드는 데이터베이스를 조회하여 해당 토픽에 연결된 인스턴스가 존재하는지 확인합니다.
     * 백엔드에서 DTO 제공: 백엔드는 토픽의 상세 정보와 함께, 인스턴스의 존재 유무에 따른 수정 가능한 필드 정보를 DTO로 포장하여 프론트엔드에 응답합니다.
     * 프론트엔드에서 수정 불가능한 필드 처리: 프론트엔드는 백엔드로부터 받은 DTO를 기반으로, 인스턴스 존재 여부에 따라 수정 불가능한 필드를 사용자 UI에서 비활성화(disable)하거나 숨깁니다.
     * 이는 사용자가 어떤 필드를 수정할 수 있는지 명확하게 인지할 수 있도록 도와줍니다.
     * 사용자가 수정 완료 후 저장 요청: 사용자가 수정을 완료하고 저장 버튼을 클릭하면, 프론트엔드는 수정된 데이터를 백엔드에 전송합니다.
     * 백엔드에서 최종 데이터 처리: 백엔드는 받은 데이터의 유효성을 검증하고, 조건에 따라 토픽을 업데이트합니다.
     * */
    public Topic updateTopic(Long id, TopicDTO topicDTO) {
        Topic topic = getTopicById(id);
        // 서버에서 한번 더 검사
        boolean hasInstance = !topic.getInstanceList().isEmpty();
        if (hasInstance) {
            topic.hasInstanceUpdate(topicDTO.description());
        } else {
            topic.hasNotInstanceUpdate(topicDTO.title(), topicDTO.description(), topicDTO.tags(), topicDTO.point_per_person());
        }
        return topicRepository.save(topic);
    }

    // 토픽 삭제 요청
    public void deleteTopic(Long id) {
        Topic topic = getTopicById(id);
        topicRepository.delete(topic);
    }
}
