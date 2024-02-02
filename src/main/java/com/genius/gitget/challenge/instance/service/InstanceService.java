package com.genius.gitget.challenge.instance.service;

import com.genius.gitget.challenge.instance.dto.crud.InstanceCreateRequest;
import com.genius.gitget.challenge.instance.dto.crud.InstanceDetailResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstancePagingResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstanceUpdateRequest;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.genius.gitget.global.util.exception.ErrorCode.INSTANCE_NOT_FOUND;
import static com.genius.gitget.global.util.exception.ErrorCode.TOPIC_NOT_FOUND;

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
                                MultipartFile multipartFile, String type) throws IOException {
        Topic topic = topicRepository.findById(instanceCreateRequest.topicId())
                .orElseThrow(() -> new BusinessException(TOPIC_NOT_FOUND));

        Files uploadedFile = filesService.uploadFile(multipartFile, type);

        Instance instance = Instance.builder()
                .title(instanceCreateRequest.title())
                .tags(instanceCreateRequest.tags())
                .description(instanceCreateRequest.description())
                .pointPerPerson(instanceCreateRequest.pointPerPerson())
                .notice(instanceCreateRequest.notice())
                .startedDate(instanceCreateRequest.startedAt())
                .completedDate(instanceCreateRequest.completedAt())
                .progress(Progress.PREACTIVITY)
                .build();

        instance.setTopic(topic);
        instance.setFiles(uploadedFile);

        Instance savedInstance = instanceRepository.save(instance);

        return savedInstance.getId();
    }

    // 인스턴스 리스트 조회
    public Page<InstancePagingResponse> getAllInstances(Pageable pageable) throws IOException {
        Page<Instance> instances = instanceRepository.findAllById(pageable);
        return instances.map(this::mapToInstancePagingResponse);
    }

    // 인스턴스 단건 조회
    public InstanceDetailResponse getInstanceById(Long id) throws IOException {
        Instance instanceDetails = instanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));
        return InstanceDetailResponse.createByEntity(instanceDetails, instanceDetails.getFiles());
    }

    // 인스턴스 삭제
    @Transactional
    public void deleteInstance(Long id) throws IOException{
        Instance instance = instanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));

        Optional<Files> findInstanceFile = instance.getFiles();
        Long findInstanceFileId = findInstanceFile.get().getId();

        filesService.deleteFile(findInstanceFileId);
        instance.setFiles(null);
        instanceRepository.delete(instance);
    }

    // 인스턴스 수정
    @Transactional
    public Long updateInstance(Long id,  InstanceUpdateRequest instanceUpdateRequest,
                               MultipartFile multipartFile, String type) throws IOException{
        Instance existingInstance = instanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));

        Optional<Files> findInstanceFile = existingInstance.getFiles();
        Long findInstanceFileId = findInstanceFile.get().getId();
        filesService.updateFile(findInstanceFileId, multipartFile);

        existingInstance.updateInstance(instanceUpdateRequest.description(), instanceUpdateRequest.notice(), instanceUpdateRequest.pointPerPerson(),
                instanceUpdateRequest.startedAt(), instanceUpdateRequest.completedAt());

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
