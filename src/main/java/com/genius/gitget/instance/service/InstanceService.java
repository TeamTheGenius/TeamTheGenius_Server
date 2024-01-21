package com.genius.gitget.instance.service;

import com.genius.gitget.instance.dto.InstanceDTO;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.util.exception.BusinessException;
import com.genius.gitget.instance.domain.Instance;
import com.genius.gitget.instance.repository.InstanceRepository;
import com.genius.gitget.topic.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstanceService {

    private final InstanceRepository instanceRepository;
    private final TopicRepository topicRepository;

    // 인스턴스 생성
    public Instance createInstance(Long topicId, InstanceDTO instanceDTO) {
        // 해당 토픽있는지 확인.
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new BusinessException("Topic not found with id: " + topicId));

        Instance instance = Instance.builder()
                .description(instanceDTO.description())
                .point_per_person(instanceDTO.pointPerPerson())
                .startedDate(instanceDTO.startedAt())
                .completedDate(instanceDTO.completedAt())
                .topic(topic)
                .build();

        return instanceRepository.save(instance);
    }

    // 인스턴스 리스트 조회
    public Page<Instance> getAllInstances(Pageable pageable) {
        return instanceRepository.findAllSortById(pageable);
    }

    // 인스턴스 단건 조회
    public Instance getInstanceById(Long id) {
        return instanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Instance not found with id: " + id));
    }

    // 인스턴스 삭제
    public void deleteInstance(Long id) {
        Instance instance = instanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Instance not found with id: " + id));
        instanceRepository.delete(instance);
    }

    // 인스턴스 수정
    public Instance updateInstance(Long id, InstanceDTO instanceDTO) {
        Instance existingInstance = instanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Instance not found with id: " + id));

        existingInstance.updateInstance(instanceDTO.description(), instanceDTO.pointPerPerson(),
                instanceDTO.startedAt(), instanceDTO.completedAt());

        return instanceRepository.save(existingInstance);
    }
}
