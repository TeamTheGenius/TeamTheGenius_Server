package com.genius.gitget.global.security.service;

import static com.genius.gitget.global.security.constants.JwtRule.ACCESS_HEADER;
import static com.genius.gitget.global.security.constants.JwtRule.ACCESS_PREFIX;
import static com.genius.gitget.global.security.constants.JwtRule.REFRESH_PREFIX;
import static com.genius.gitget.global.util.exception.ErrorCode.INVALID_JWT;
import static com.genius.gitget.global.util.exception.ErrorCode.JWT_NOT_FOUND_IN_COOKIE;
import static com.genius.gitget.global.util.exception.ErrorCode.JWT_NOT_FOUND_IN_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.security.domain.Token;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.security.repository.TokenRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import jakarta.servlet.http.Cookie;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
@ActiveProfiles({"jwt"})
class JwtFacadeImplTest {
    User user;
    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private JwtFacade jwtFacade;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .providerInfo(ProviderInfo.GITHUB)
                .nickname("nickname")
                .identifier("identifier")
                .role(Role.USER)
                .tags("interest1,interest2")
                .information("information")
                .build());
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();
    }

    @AfterEach
    void clearMongo() {
        tokenRepository.deleteAll();
    }


    @Nested
    @DisplayName("JWT 생성 시")
    class describe_create_jwt {
        @Nested
        @DisplayName("사용자의 정보를 전달하면")
        class context_pass_user_info {
            @Test
            @DisplayName("Access token을 생성하여 Authorization 헤더에 담는다.")
            public void it_returns_headers_that_contain_access() {
                String accessToken = jwtFacade.generateAccessToken(response, user);
                Collection<String> headerNames = response.getHeaderNames();

                assertThat(headerNames).contains(ACCESS_HEADER.getValue());
                assertThat(response.getHeader(ACCESS_HEADER.getValue())).contains(accessToken);
            }

            @Test
            @DisplayName("Refresh token을 생성하여 Cookie에 담는다.")
            public void it_returns_cookie_that_contain_refresh() {
                String refreshToken = jwtFacade.generateRefreshToken(response, user);
                Cookie cookie = response.getCookies()[0];

                assertThat(cookie.getValue()).isEqualTo(refreshToken);
                assertThat(cookie.getSecure()).isTrue();
                assertThat(cookie.getPath()).isEqualTo("/");
            }
        }
    }

    @Nested
    @DisplayName("JWT 유효성 확인 시")
    class describe_validate_jwt {
        @Nested
        @DisplayName("Access token을 전달한 경우")
        class context_pass_access {
            @Test
            @DisplayName("유효기간이 만료되지 않았고, 토큰이 유효하다면 true를 반환한다.")
            public void it_returns_true_when_token_not_expired_and_valid() {
                String accessToken = jwtFacade.generateAccessToken(response, user);
                boolean isAccessValid = jwtFacade.validateAccessToken(accessToken);
                assertThat(isAccessValid).isTrue();
            }

            @Test
            @DisplayName("토큰이 유효하지 않는다면 BusinessException 예외를 발생한다.")
            public void it_throws_BusinessException_when_token_invalid() {
                String accessToken = "invalid access token";
                assertThatThrownBy(() -> jwtFacade.validateAccessToken(accessToken))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(INVALID_JWT.getMessage());
            }
        }

        @Nested
        @DisplayName("Refresh token을 전달한 경우")
        class context_pass_refresh {
            @Test
            @DisplayName("토큰이 유효하고, DB에 저장된 토큰과 같다면 true를 반환한다.")
            public void it_returns_true_when_token_valid_and_stored_db() {
                String refreshToken = jwtFacade.generateRefreshToken(response, user);
                boolean isRefreshValid = jwtFacade.validateRefreshToken(refreshToken, user.getIdentifier());
                assertThat(isRefreshValid).isTrue();
            }

            @Test
            @DisplayName("토큰이 유효하지 않는다면 BusinessException 예외를 발생한다.")
            public void it_throws_BusinessException_when_token_invalid() {
                String refreshToken = "invalid refresh token";
                assertThatThrownBy(() -> jwtFacade.validateRefreshToken(refreshToken, user.getIdentifier()))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(INVALID_JWT.getMessage());
            }

            @Test
            @DisplayName("DB에 저장된 토큰과 같지 않다면 false를 반환한다.")
            public void it_returns_false_when_not_match_db() {
                String refreshToken = jwtFacade.generateRefreshToken(response, user);
                tokenRepository.save(new Token(user.getIdentifier(), "invalid refresh token"));

                boolean isRefreshValid = jwtFacade.validateRefreshToken(refreshToken, user.getIdentifier());
                assertThat(isRefreshValid).isFalse();
            }
        }
    }

    @Nested
    @DisplayName("HttpServletRequest로부터")
    class describe_from_HttpServletRequest {
        @Nested
        @DisplayName("access token을 추출하는 경우")
        class context_resolve_access {
            @Test
            @DisplayName("Authorization 헤더에 유효한 토큰이 있는 경우 access token을 반환한다.")
            public void it_returns_access_token() {
                String accessToken = jwtFacade.generateAccessToken(response, user);
                request.addHeader(ACCESS_HEADER.getValue(), ACCESS_PREFIX.getValue() + accessToken);

                String resolvedAccessToken = jwtFacade.resolveAccessToken(request);
                assertThat(accessToken).isEqualTo(resolvedAccessToken);
            }

            @Test
            @DisplayName("Authorization 헤더에 빈 문자열이 있는 경우 BusinessException 예외가 발생한다.")
            public void it_throws_BusinessException_when_authorization_is_empty() {
                jwtFacade.generateAccessToken(response, user);
                request.addHeader(ACCESS_HEADER.getValue(), "");
                assertThatThrownBy(() -> jwtFacade.resolveAccessToken(request))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(JWT_NOT_FOUND_IN_HEADER.getMessage());
            }

            @Test
            @DisplayName("Authorization 헤더가 null인 경우 BusinessException 예외가 발생한다.")
            public void it_throws_BusinessException_when_authorization_is_null() {
                assertThatThrownBy(() -> jwtFacade.resolveAccessToken(request))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(JWT_NOT_FOUND_IN_HEADER.getMessage());
            }
        }

        @Nested
        @DisplayName("refresh token을 추출하는 경우")
        class context_resolve_refresh {
            @Test
            @DisplayName("Cookie에 유효한 토큰이 있는 경우 Refresh token을 반환한다.")
            public void it_returns_refresh_token() {
                String refreshToken = jwtFacade.generateRefreshToken(response, user);

                request.setCookies(new Cookie(REFRESH_PREFIX.getValue(), refreshToken));
                String resolvedRefreshToken = jwtFacade.resolveRefreshToken(request);
                assertThat(refreshToken).isEqualTo(resolvedRefreshToken);
            }

            @Test
            @DisplayName("Cookie에 refresh 토큰이 없는 경우 BusinessException 예외가 발생한다.")
            public void it_throws_businessException_when_token_not_exist() {
                assertThatThrownBy(() -> jwtFacade.resolveRefreshToken(request))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(JWT_NOT_FOUND_IN_COOKIE.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("사용자의 식별자를 확인 시")
    class describe_check_user_identifier {
        @Nested
        @DisplayName("Refresh token을 전달했을 때")
        class context_pass_refresh_token {
            @Test
            @DisplayName("토큰이 유효하다면 사용자의 identifier를 반환한다.")
            public void it_returns_identifier_when_refresh_valid() {
                String refreshToken = jwtFacade.generateRefreshToken(response, user);
                String identifier = jwtFacade.getIdentifierFromRefresh(refreshToken);
                assertThat(user.getIdentifier()).isEqualTo(identifier);
            }

            @Test
            @DisplayName("토큰이 유효하지 않으면 BusinessException 예외가 발생한다.")
            public void it_throws_businessException_when_refresh_invalid() {
                String invalidRefreshToken = "invalid refresh token";
                assertThatThrownBy(() -> jwtFacade.getIdentifierFromRefresh(invalidRefreshToken))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(INVALID_JWT.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("SecurityContext에 저장할 객체를 받으려 할 때")
    class describe_try_to_get_authentication {
        @Nested
        @DisplayName("Access token을 전달한 경우")
        class context_pass_access_token {
            @Test
            @DisplayName("Authentication를 반환받을 수 있다.")
            public void it_returns_identifier() {
                String accessToken = jwtFacade.generateAccessToken(response, user);
                Authentication authentication = jwtFacade.getAuthentication(accessToken);

                String identifier = ((UserPrincipal) authentication.getPrincipal()).getUser().getIdentifier();
                assertThat(identifier).isEqualTo(user.getIdentifier());
            }
        }
    }

    @Nested
    @DisplayName("로그아웃 요청을 받았을 때")
    class describe_logout {
        @Nested
        @DisplayName("사용자의 식별자 정보를 전달하면")
        class context_pass_identifier {
            @Test
            @DisplayName("cookie를 비우고, DB의 토큰 정보도 삭제한다.")
            public void it_clear_cookie_and_db() {
                jwtFacade.generateRefreshToken(response, user);
                jwtFacade.logout(response, user.getIdentifier());

                assertThat(tokenRepository.findById(user.getIdentifier())).isNotPresent();
            }
        }
    }
}