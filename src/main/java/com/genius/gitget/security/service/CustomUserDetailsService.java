package com.genius.gitget.security.service;

import static com.genius.gitget.util.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.genius.gitget.security.domain.UserPrincipal;
import com.genius.gitget.user.domain.User;
import com.genius.gitget.user.repository.UserRepository;
import com.genius.gitget.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(Long.valueOf(username))
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));

        return new UserPrincipal(user);
    }
}
