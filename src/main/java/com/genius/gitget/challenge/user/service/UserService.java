package com.genius.gitget.challenge.user.service;

import static com.genius.gitget.global.util.exception.ErrorCode.DUPLICATED_NICKNAME;
import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_TOKEN_NOT_FOUND;
import static com.genius.gitget.global.util.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.genius.gitget.challenge.certification.util.EncryptUtil;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.dto.SignupRequest;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.util.exception.BusinessException;
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
    private final EncryptUtil encryptUtil;


    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
    }

    public User findUserByIdentifier(String identifier) {
        return userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
    }

    @Transactional
    public Long save(User user) {
        return userRepository.saveAndFlush(user).getId();
    }

    @Transactional
    public Long signup(SignupRequest requestUser) {
        User targetUser = findUserByIdentifier(requestUser.identifier());

        //TODO: Converter 클래스 만들어서 적용하기
        String interest = String.join(",", requestUser.interest());

        targetUser.updateUser(requestUser.nickname(),
                requestUser.information(),
                interest);
        targetUser.updateRole(Role.USER);

        return targetUser.getId();
    }

    public void isNicknameDuplicate(String nickname) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new BusinessException(DUPLICATED_NICKNAME);
        }
    }

    public String getGithubToken(User user) {
        String githubToken = user.getGithubToken();
        if (githubToken == null || githubToken.isEmpty() || githubToken.isBlank()) {
            throw new BusinessException(GITHUB_TOKEN_NOT_FOUND);
        }
        return encryptUtil.decryptPersonalToken(githubToken);
    }
}
