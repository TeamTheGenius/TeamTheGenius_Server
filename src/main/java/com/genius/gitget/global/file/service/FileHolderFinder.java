package com.genius.gitget.global.file.service;

import static com.genius.gitget.global.util.exception.ErrorCode.INSTANCE_NOT_FOUND;
import static com.genius.gitget.global.util.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.genius.gitget.global.util.exception.ErrorCode.TOPIC_NOT_FOUND;

import com.genius.gitget.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.file.domain.FileHolder;
import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileHolderFinder {
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final InstanceRepository instanceRepository;

    public FileHolder findByInfo(Long id, FileType fileType) {
        switch (fileType) {
            case TOPIC -> {
                return topicRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(TOPIC_NOT_FOUND));
            }
            case INSTANCE -> {
                return instanceRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));
            }
            case PROFILE -> {
                return userRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
            }
        }
        throw new BusinessException();
    }
}
