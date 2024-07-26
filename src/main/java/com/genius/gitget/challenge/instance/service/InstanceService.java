package com.genius.gitget.challenge.instance.service;

import static com.genius.gitget.global.util.exception.ErrorCode.INSTANCE_NOT_FOUND;
import static com.genius.gitget.global.util.exception.ErrorCode.INVALID_INSTANCE_DATE;
import static com.genius.gitget.global.util.exception.ErrorCode.TOPIC_NOT_FOUND;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.dto.crud.InstanceUpdateDTO;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.repository.TopicRepository;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstanceService {
    private final InstanceRepository instanceRepository;
    private final TopicRepository topicRepository;
    private final FilesService filesService;

    @NotNull
    private static String getUuid() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "").substring(0, 16);
    }

    private void validatePeriod(LocalDate startDate, LocalDate completeDate, LocalDate currentDate) {
        if (currentDate.isAfter(startDate) || currentDate.isAfter(completeDate)) {
            throw new BusinessException(INVALID_INSTANCE_DATE);
        }
    }

    // 인스턴스 생성
    @Transactional
    public Long createInstance(Instance instance, Long id, LocalDate startDate, LocalDate completeDate,
                               LocalDate currentDate) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TOPIC_NOT_FOUND));

        validatePeriod(startDate, completeDate, currentDate);
        String uuid = getUuid();
        instance.setInstanceUUID(uuid);
        instance.setTopic(topic);

        return instanceRepository.save(instance).getId();
    }

    // 인스턴스 수정
    @Transactional
    public Long modifyInstance(Long id, InstanceUpdateDTO instanceUpdateDTO) {
        Instance existingInstance = instanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));

        existingInstance.updateInstance(instanceUpdateDTO.description(), instanceUpdateDTO.notice(),
                instanceUpdateDTO.pointPerPerson(), instanceUpdateDTO.startedDate(),
                instanceUpdateDTO.completedDate(), instanceUpdateDTO.certificationMethod());

        Instance savedInstance = instanceRepository.save(existingInstance);

        return savedInstance.getId();
    }

    // 인스턴스 삭제
    @Transactional
    public void removeInstance(Long id) {
        Instance instance = instanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));

        Files files = instance.getFiles().orElse(null);
        Long filesId = files != null ? files.getId() : null;

        if (filesId != null) {
            filesService.deleteFile(filesId);
            instance.setFiles(null);
        }
        instanceRepository.delete(instance);
    }

    // 인스턴스 단건 조회
    public Instance findInstanceById(Long id) {
        return instanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));
    }

    // 인스턴스 리스트 조회
    public Page<Instance> findAllInstances(Pageable pageable) {
        return instanceRepository.findAllById(pageable);
    }

    // 특정 토픽에 대한 리스트 조회
    public Page<Instance> getAllInstancesOfSpecificTopic(Pageable pageable, Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return instanceRepository.findInstancesByTopicId(pageable, topic.getId());
    }
}
