package com.genius.gitget.challenge.instance.facade;

import com.genius.gitget.challenge.instance.dto.crud.InstanceCreateRequest;
import com.genius.gitget.challenge.instance.dto.crud.InstanceDetailResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstancePagingResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstanceUpdateRequest;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InstanceFacade {

    Long createInstance(InstanceCreateRequest instanceCreateRequest, LocalDate currentDate);

    Page<InstancePagingResponse> findAllInstances(Pageable pageable);

    Page<InstancePagingResponse> getAllInstancesOfSpecificTopic(Pageable pageable, Long id);

    InstanceDetailResponse findOne(Long id);

    void removeInstance(Long id);

    Long modifyInstance(Long id, InstanceUpdateRequest instanceUpdateRequest);

}
