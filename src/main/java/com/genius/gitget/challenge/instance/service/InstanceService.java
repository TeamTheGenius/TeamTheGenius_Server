package com.genius.gitget.challenge.instance.service;

import static com.genius.gitget.global.util.exception.ErrorCode.INSTANCE_NOT_FOUND;
import static com.genius.gitget.global.util.exception.ErrorCode.INVALID_INSTANCE_DATE;
import static com.genius.gitget.global.util.exception.ErrorCode.TOPIC_NOT_FOUND;

import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.dto.crud.InstanceCreateRequest;
import com.genius.gitget.challenge.instance.dto.crud.InstanceDetailResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstancePagingResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstanceUpdateRequest;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstanceService {
    private final InstanceRepository instanceRepository;
    private final TopicRepository topicRepository;
    private final FilesService filesService;

    // 인스턴스 생성
    @Transactional
    public Long createInstance(InstanceCreateRequest instanceCreateRequest,
                               MultipartFile multipartFile, String type,
                               LocalDate currentDate) {
        // 토픽 조회
        Topic topic = topicRepository.findById(instanceCreateRequest.topicId())
                .orElseThrow(() -> new BusinessException(TOPIC_NOT_FOUND));

        // 파일 업로드
        FileType fileType = FileType.findType(type);
        Files uploadedFile = filesService.uploadFile(topic.getFiles(), multipartFile, fileType);

        // 인스턴스 생성 일자 검증
        validatePeriod(instanceCreateRequest, currentDate);

        // 인스턴스 고유 uuid 생성
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-", "").substring(0, 16);

        // from dto to entity 변환 및 uuid 설정
        Instance instance = Instance.createByRequest(instanceCreateRequest);
        instance.setInstanceUUID(uuid);

        // 연관 관계 설정
        instance.setTopic(topic);
        instance.setFiles(uploadedFile);

        return instanceRepository.save(instance).getId();
    }

    private void validatePeriod(InstanceCreateRequest instanceCreateRequest, LocalDate currentDate) {
        LocalDate startedDate = instanceCreateRequest.startedAt().toLocalDate();
        LocalDate completedDate = instanceCreateRequest.completedAt().toLocalDate();

        if (currentDate.isAfter(startedDate) || currentDate.isAfter(completedDate)) {
            throw new BusinessException(INVALID_INSTANCE_DATE);
        }
    }

    // 인스턴스 리스트 조회
    public Page<InstancePagingResponse> getAllInstances(Pageable pageable) {
        Page<Instance> instances = instanceRepository.findAllById(pageable);
        return instances.map(this::mapToInstancePagingResponse);
    }


    // 특정 토픽에 대한 리스트 조회
    public Page<InstancePagingResponse> getAllInstancesOfSpecificTopic(Pageable pageable, Long id) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        Page<Instance> instancesByTopicId = instanceRepository.findInstancesByTopicId(pageable, topic.getId());
        return instancesByTopicId.map(this::mapToInstancePagingResponse);
    }


    // 인스턴스 단건 조회
    public InstanceDetailResponse getInstanceById(Long id) {
        Instance instanceDetails = instanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));
        return InstanceDetailResponse.createByEntity(instanceDetails, instanceDetails.getFiles());
    }


    // 인스턴스 삭제
    @Transactional
    public void deleteInstance(Long id) {
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


    // 인스턴스 수정
    @Transactional
    public Long updateInstance(Long id, InstanceUpdateRequest instanceUpdateRequest, MultipartFile multipartFile,
                               String type) {
        Instance existingInstance = instanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));

        Optional<Files> findInstanceFile = existingInstance.getFiles();
        Long findInstanceFileId = findInstanceFile.get().getId();
        filesService.updateFile(findInstanceFileId, multipartFile);

        existingInstance.updateInstance(instanceUpdateRequest.description(), instanceUpdateRequest.notice(),
                instanceUpdateRequest.pointPerPerson(), instanceUpdateRequest.startedAt(),
                instanceUpdateRequest.completedAt(), instanceUpdateRequest.certificationMethod());

        Instance savedInstance = instanceRepository.save(existingInstance);

        return savedInstance.getId();
    }

    private InstancePagingResponse mapToInstancePagingResponse(Instance instance) {
        try {
            return InstancePagingResponse.createByEntity(instance, instance.getFiles());
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }
}
