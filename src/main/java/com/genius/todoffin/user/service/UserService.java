package com.genius.todoffin.user.service;

import com.genius.todoffin.user.domain.Role;
import com.genius.todoffin.user.domain.User;
import com.genius.todoffin.user.dto.SignupRequest;
import com.genius.todoffin.user.repository.UserRepository;
import com.genius.todoffin.util.exception.BusinessException;
import com.genius.todoffin.util.exception.ErrorCode;
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


    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public User findUserByIdentifier(String email) {
        return userRepository.findByIdentifier(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional
    public Long signup(SignupRequest requestUser) {
        User targetUser = findUserByIdentifier(requestUser.email());

        //TODO: Converter 클래스 만들어서 적용하기
        String interest = String.join(",", requestUser.interest());

        targetUser.updateUser(requestUser.nickname(),
                requestUser.information(),
                interest);
        targetUser.updateRole(Role.USER);

        return targetUser.getId();
    }
}
