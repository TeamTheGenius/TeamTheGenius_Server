package com.genius.gitget.challenge.user.service;

import static com.genius.gitget.global.util.exception.ErrorCode.ALREADY_REGISTERED;
import static com.genius.gitget.global.util.exception.ErrorCode.DUPLICATED_NICKNAME;
import static com.genius.gitget.global.util.exception.ErrorCode.NOT_AUTHENTICATED_USER;
import static com.genius.gitget.store.item.domain.ItemCategory.PROFILE_FRAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.dto.SignupRequest;
import com.genius.gitget.challenge.user.facade.UserFacade;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.dto.AuthResponse;
import com.genius.gitget.global.security.dto.SignupResponse;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.store.item.domain.EquipStatus;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.Orders;
import com.genius.gitget.store.item.repository.ItemRepository;
import com.genius.gitget.store.item.repository.OrdersRepository;
import com.genius.gitget.util.store.StoreFactory;
import com.genius.gitget.util.user.UserFactory;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
class UserFacadeTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private UserFacade userFacade;

    @Value("${github.yeon-githubId}")
    private String githubId;

    private User user;

    @Nested
    @DisplayName("회원 가입 시도 시")
    class context_try_register {
        @Nested
        @DisplayName("사용자의 정보 확인했을 때")
        class describe_check_user_info {
            @Test
            @DisplayName("어드민 깃허브 계정에 해당하는 경우, ADMIN으로 설정된다.")
            public void it_set_role_admin() {
                userRepository.save(UserFactory.createUnregistered(githubId));
                SignupResponse signupResponse = userFacade.signup(getSignupRequest(githubId));

                Optional<User> optionalUser = userRepository.findById(signupResponse.userId());
                assertThat(optionalUser).isPresent();
                assertThat(optionalUser.get().getId()).isEqualTo(signupResponse.userId());
                assertThat(optionalUser.get().getRole()).isEqualTo(Role.ADMIN);
            }

            @Test
            @DisplayName("어드민 깃허브 계정에 해당하지 않는 경우, USER로 설정된다.")
            public void it_set_role_user() {
                String identifier = "identifier";
                userRepository.save(UserFactory.createUnregistered(identifier));
                SignupResponse signupResponse = userFacade.signup(getSignupRequest(identifier));

                Optional<User> optionalUser = userRepository.findById(signupResponse.userId());
                assertThat(optionalUser).isPresent();
                assertThat(optionalUser.get().getId()).isEqualTo(signupResponse.userId());
                assertThat(optionalUser.get().getRole()).isEqualTo(Role.USER);
            }

            @Test
            @DisplayName("이미 회원가입된 사용자인 경우 ALREADY_REGISTERED 예외가 발생한다.")
            public void it_throw_ALREADY_REGISTERED_exception() {
                userRepository.save(UserFactory.createUnregistered(githubId));
                SignupRequest signupRequest = getSignupRequest(githubId);
                userFacade.signup(signupRequest);

                assertThatThrownBy(() -> userFacade.signup(signupRequest))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ALREADY_REGISTERED.getMessage());
            }

            SignupRequest getSignupRequest(String identifier) {
                return SignupRequest.builder()
                        .identifier(identifier)
                        .information("information")
                        .interest(List.of("java", "BE"))
                        .nickname("nickname")
                        .build();
            }
        }
    }

    @Nested
    @DisplayName("사용자의 닉네임 중복 확인 시")
    class context_check_nickname_duplication {
        String nickname = "nickname";

        @Nested
        @DisplayName("닉네임을 전달했을 때")
        class describe_pass_nickname {
            @Test
            @DisplayName("닉네임이 기존에 존재하지 않는다면, 예외가 발생하지 않는다.")
            public void it_not_throw_exception_nickname_not_exist() {
                assertThatNoException().isThrownBy(
                        () -> userFacade.isNicknameDuplicate(nickname)
                );
            }

            @Test
            @DisplayName("닉네임이 기존에 존재한다면, DUPLICATED_NICKNAME 예외가 발생한다.")
            public void it_throw_DUPLICATED_NICKNAME_exception_nickname_exist() {
                user = userRepository.save(UserFactory.createUnregistered(githubId));
                user.updateUserInformation(nickname, "information");

                assertThatThrownBy(() -> userFacade.isNicknameDuplicate(nickname))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(DUPLICATED_NICKNAME.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("토큰 발급 이후 사용자의 정보 조회 시")
    class context_inquiry_user_after_issue_token {
        @Nested
        @DisplayName("사용자의 identifier 전달 시")
        class describe_pass_identifier {
            Item item;
            Orders orders;

            @BeforeEach
            void setup() {
                user = userRepository.save(UserFactory.createByInfo(githubId, Role.USER));
                item = itemRepository.save(StoreFactory.createItem(PROFILE_FRAME));
            }

            @Test
            @DisplayName("identifier에 해당하는 사용자가 없으면 MEMBER_NOT_FOUND 예외가 발생한다.")
            public void it_throw_MEMBER_NOT_FOUND_exception() {
                assertThatThrownBy(() -> userFacade.getUserAuthInfo("identifier"))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
            }

            @Test
            @DisplayName("사용자가 프로필 프레임을 장착하고 있지 않을 때, 프레임 정보에 null이 담긴다.")
            public void it_return_null_when_not_use_frame() {
                AuthResponse authResponse = userFacade.getUserAuthInfo(githubId);
                assertThat(authResponse.role()).isEqualTo(Role.USER);
                assertThat(authResponse.frameId()).isNull();
            }

            @Test
            @DisplayName("사용자가 프로필 프레임을 장착하고 있을 때, 프로필 정보에 아이템의 PK가 담긴다.")
            public void it_return_itemPK_when_use_frame() {
                orders = ordersRepository.save(StoreFactory.createOrders(user, item, PROFILE_FRAME, 3));
                orders.updateEquipStatus(EquipStatus.IN_USE);

                AuthResponse authResponse = userFacade.getUserAuthInfo(githubId);
                assertThat(authResponse.role()).isEqualTo(Role.USER);
                assertThat(authResponse.frameId()).isEqualTo(item.getIdentifier());
            }
        }
    }

    @Nested
    @DisplayName("인증 가능한 사용자 조회 시")
    class context_inquiry_auth_user_info {
        @Nested
        @DisplayName("사용자의 identifier 전달 시")
        class describe_pass_identifier {
            @Test
            @DisplayName("회원 가입이 완료된 사용자라면 User 엔티티를 반환한다.")
            public void it_return_user_when_registered() {
                user = userRepository.save(UserFactory.createByInfo(githubId, Role.USER));

                User authUser = userFacade.getAuthUser(user.getIdentifier());

                assertThat(authUser.getId()).isEqualTo(user.getId());
                assertThat(authUser.getIdentifier()).isEqualTo(user.getIdentifier());
            }

            @Test
            @DisplayName("회원 가입이 안 된 사용자라면 NOT_AUTHENTICATED_USER 예외가 발생한다.")
            public void it_throws_NOT_AUTHENTICATED_USER_exception() {
                user = userRepository.save(UserFactory.createUnregistered(githubId));

                assertThatThrownBy(() -> userFacade.getAuthUser(githubId))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(NOT_AUTHENTICATED_USER.getMessage());
            }
        }
    }
}