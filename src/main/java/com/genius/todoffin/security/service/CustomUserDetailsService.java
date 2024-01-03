package com.genius.todoffin.security.service;

import static com.genius.todoffin.util.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.genius.todoffin.security.domain.UserPrincipal;
import com.genius.todoffin.user.domain.User;
import com.genius.todoffin.user.repository.UserRepository;
import com.genius.todoffin.util.exception.BusinessException;
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
