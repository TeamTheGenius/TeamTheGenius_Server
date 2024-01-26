package com.genius.gitget.util;

import com.genius.gitget.global.security.service.CustomUserDetailsService;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.dto.SignupRequest;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.challenge.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
@Slf4j
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    private final UserRepository userRepository;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        User user = User.builder()
                .providerInfo(customUser.providerInfo())
                .identifier(customUser.identifier())
                .role(Role.NOT_REGISTERED)
                .build();

        SignupRequest signupRequest = SignupRequest.builder()
                .identifier(customUser.identifier())
                .interest(List.of("FE", "BE"))
                .nickname(customUser.nickname())
                .information(customUser.information())
                .build();

        userRepository.save(user);
        Long signupId = userService.signup(signupRequest);

        UserDetails principal = customUserDetailsService.loadUserByUsername(String.valueOf(signupId));
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(),
                principal.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        return securityContext;
    }
}
