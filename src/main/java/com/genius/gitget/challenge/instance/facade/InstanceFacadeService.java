package com.genius.gitget.challenge.instance.facade;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.dto.crud.InstanceCreateRequest;
import com.genius.gitget.challenge.instance.dto.crud.InstanceDetailResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstancePagingResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstanceUpdateDTO;
import com.genius.gitget.challenge.instance.dto.crud.InstanceUpdateRequest;
import com.genius.gitget.challenge.instance.service.InstanceService;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesManager;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Slf4j
@Transactional
public class InstanceFacadeService implements InstanceFacade {
    private final FilesManager filesManager;
    private final InstanceService instanceService;

    // 인스턴스 생성
    @Override
    public Long createInstance(InstanceCreateRequest instanceCreateRequest, LocalDate currentDate) {
        Instance instance = InstanceCreateRequest.from(instanceCreateRequest);
        LocalDate startDate = instanceCreateRequest.startedAt().toLocalDate();
        LocalDate completeDate = instanceCreateRequest.completedAt().toLocalDate();

        return instanceService.createInstance(instance, instanceCreateRequest.topicId(), startDate, completeDate,
                currentDate);
    }

    // 인스턴스 수정
    @Override
    public Long modifyInstance(Long id, InstanceUpdateRequest instanceUpdateRequest) {
        InstanceUpdateDTO updateDTO = InstanceUpdateDTO.of(instanceUpdateRequest.description(),
                instanceUpdateRequest.notice(), instanceUpdateRequest.pointPerPerson(),
                instanceUpdateRequest.startedAt(),
                instanceUpdateRequest.completedAt(), instanceUpdateRequest.certificationMethod());

        return instanceService.modifyInstance(id, updateDTO);
    }

    @Override
    public void removeInstance(Long id) {
        instanceService.removeInstance(id);
    }

    // 인스턴스 단건 조회
    @Override
    public InstanceDetailResponse findOne(Long id) {
        Instance instance = instanceService.findInstanceById(id);
        FileResponse fileResponse = filesManager.convertToFileResponse(instance.getFiles());

        return InstanceDetailResponse.of(instance, fileResponse);
    }

    // 인스턴스 전체 조회
    @Override
    public Page<InstancePagingResponse> findAllInstances(Pageable pageable) {
        Page<Instance> allInstances = instanceService.findAllInstances(pageable);
        return allInstances.map(this::mapToInstancePagingResponse);
    }

    @Override
    public Page<InstancePagingResponse> getAllInstancesOfSpecificTopic(Pageable pageable, Long id) {
        Page<Instance> allInstancesOfSpecificTopic = instanceService.getAllInstancesOfSpecificTopic(pageable, id);
        return allInstancesOfSpecificTopic.map(this::mapToInstancePagingResponse);
    }

    private InstancePagingResponse mapToInstancePagingResponse(Instance instance) {
        FileResponse fileResponse = filesManager.convertToFileResponse(instance.getFiles());
        return InstancePagingResponse.of(instance, fileResponse);
    }
}
