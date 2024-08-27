package com.genius.gitget.challenge.user.facade;

import static com.genius.gitget.global.util.exception.ErrorCode.ALREADY_REGISTERED;
import static com.genius.gitget.global.util.exception.ErrorCode.DUPLICATED_NICKNAME;
import static com.genius.gitget.global.util.exception.ErrorCode.NOT_AUTHENTICATED_USER;

import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.dto.SignupRequest;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.security.dto.AuthResponse;
import com.genius.gitget.global.security.dto.SignupResponse;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.service.OrdersService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceFacade implements UserFacade {
    private final UserService userService;
    private final OrdersService ordersService;

    @Value("${admin.githubId}")
    private List<String> adminIds;

    @Override
    public void isNicknameDuplicate(String nickname) {
        if (userService.findByNickname(nickname).isPresent()) {
            throw new BusinessException(DUPLICATED_NICKNAME);
        }
    }

    @Override
    public SignupResponse signup(SignupRequest signupRequest) {
        User user = userService.findByIdentifier(signupRequest.identifier());

        if (user.getRole() != Role.NOT_REGISTERED) {
            throw new BusinessException(ALREADY_REGISTERED);
        }

        String interest = String.join(",", signupRequest.interest());
        user.updateUser(signupRequest.nickname(), signupRequest.information(), interest);
        updateRole(user);

        return SignupResponse.of(user.getId(), user.getIdentifier());
    }

    private void updateRole(User user) {
        if (adminIds.contains(user.getIdentifier())) {
            user.updateRole(Role.ADMIN);
            return;
        }
        user.updateRole(Role.USER);
    }

    @Override
    public AuthResponse getUserAuthInfo(String identifier) {
        User user = userService.findByIdentifier(identifier);
        Item usingFrame = ordersService.getUsingFrameItem(user.getId());
        return new AuthResponse(user.getRole(), usingFrame.getId());
    }

    @Override
    public User getAuthUser(String identifier) {
        User user = userService.findByIdentifier(identifier);
        if (!user.isRegistered()) {
            throw new BusinessException(NOT_AUTHENTICATED_USER);
        }
        return user;
    }
}
