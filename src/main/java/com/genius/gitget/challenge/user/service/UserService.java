package com.genius.gitget.challenge.user.service;

import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_TOKEN_NOT_FOUND;
import static com.genius.gitget.global.util.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.genius.gitget.challenge.certification.util.EncryptUtil;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.signout.Signout;
import com.genius.gitget.signout.SignoutRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SignoutRepository signoutRepository;
    private final EncryptUtil encryptUtil;


    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
    }

    public User findByIdentifier(String identifier) {
        return userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
    }

    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    @Transactional
    public Long save(User user) {
        return userRepository.saveAndFlush(user).getId();
    }


    public void delete(Long userId, String identifier, String reason) {
        userRepository.deleteById(userId);
        signoutRepository.save(
                Signout.builder()
                        .identifier(identifier)
                        .reason(reason)
                        .build());
    }

    // 포인트 조회
    public Long getUserPoint(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        return user.getPoint();
    }

    public String getGithubToken(User user) {
        String githubToken = user.getGithubToken();
        if (githubToken == null || githubToken.isEmpty() || githubToken.isBlank()) {
            throw new BusinessException(GITHUB_TOKEN_NOT_FOUND);
        }
        return encryptUtil.decrypt(githubToken);
    }
}