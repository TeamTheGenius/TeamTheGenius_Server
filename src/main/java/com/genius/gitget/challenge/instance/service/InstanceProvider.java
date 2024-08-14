package com.genius.gitget.challenge.instance.service;

import static com.genius.gitget.global.util.exception.ErrorCode.INSTANCE_NOT_FOUND;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InstanceProvider {
    private final InstanceRepository instanceRepository;

    public Instance findById(Long instanceId) {
        return instanceRepository.findById(instanceId)
                .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));
    }

    public List<Instance> findAllByProgress(Progress progress) {
        return instanceRepository.findAllByProgress(progress);
    }
}
