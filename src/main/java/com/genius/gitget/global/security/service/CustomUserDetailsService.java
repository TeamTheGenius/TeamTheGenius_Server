package com.genius.gitget.global.security.service;

import static com.genius.gitget.global.util.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findById(Long.valueOf(username))
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));

        return new UserPrincipal(user);
    }
}
