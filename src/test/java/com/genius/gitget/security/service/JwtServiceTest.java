package com.genius.gitget.security.service;

import static com.genius.gitget.global.security.constants.JwtRule.ACCESS_PREFIX;
import static com.genius.gitget.global.security.constants.JwtRule.REFRESH_PREFIX;
import static com.genius.gitget.global.util.exception.ErrorCode.INVALID_JWT;
import static com.genius.gitget.global.util.exception.ErrorCode.NOT_AUTHENTICATED_USER;
import static com.genius.gitget.global.util.exception.ErrorCode.TOKEN_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.JwtRule;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.security.repository.TokenRepository;
import com.genius.gitget.global.security.service.JwtService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.util.TokenTestUtil;
import com.genius.gitget.util.WithMockCustomUser;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
@ActiveProfiles({"jwt"})
class JwtServiceTest {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private TokenTestUtil tokenTestUtil;

    @Test
    @DisplayName("사용자 정보를 받아서 access-token을 생성할 수 있다.")
    public void should_generateAccess_when_passUserInfo() {
        //given
        User user = getSavedUser();
        MockHttpServletResponse response = new MockHttpServletResponse();

        //when
        String accessToken = jwtService.generateAccessToken(response, user);
        Cookie cookie = response.getCookies()[0];

        //then
        assertThat(cookie.getValue()).isEqualTo(accessToken);
        assertThat(cookie.getSecure()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/");
    }

    @Test
    @DisplayName("사용자 정보를 받아서 유효한 refresh-token를 생성할 수 있다.")
    public void should_generateRefresh_when_passUserInfo() {
        //given
        User user = getSavedUser();
        MockHttpServletResponse response = new MockHttpServletResponse();

        //when
        String refreshToken = jwtService.generateRefreshToken(response, user);
        Cookie cookie = response.getCookies()[0];

        //then
        assertThat(cookie.getValue()).isEqualTo(refreshToken);
        assertThat(cookie.getSecure()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/");
    }

    @Test
    @DisplayName("생성한 access-token이 유효하다면 true를 반환한다.")
    public void should_returnTrue_when_accessTokenIsValid() {
        //given
        User user = getSavedUser();
        MockHttpServletResponse response = new MockHttpServletResponse();

        //when
        String accessToken = jwtService.generateAccessToken(response, user);
        boolean isValid = jwtService.validateAccessToken(accessToken);

        //then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("생성한 access-token가 유효하지 않다면 false를 반환한다.")
    public void should_returnFalse_when_accessTokenIsInvalid() {
        //given

        //when
        String accessToken = "fake access token";

        //then
        assertThatThrownBy(() -> jwtService.validateAccessToken(accessToken))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("Cookie에서 access-token을 추출할 수 있다.")
    public void should_extractAccessToken_when_passTokenType() {
        //given
        User user = getSavedUser();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        //when
        String accessToken = jwtService.generateAccessToken(response, user);

        request.setCookies(new Cookie(ACCESS_PREFIX.getValue(), accessToken));
        String resolvedToken = jwtService.resolveTokenFromCookie(request, ACCESS_PREFIX);

        //then
        assertThat(accessToken).isEqualTo(resolvedToken);
    }

    @Test
    @DisplayName("Cookie에서 refresh-token을 추출할 수 있다.")
    public void should_extractRefreshToken_when_passTokenType() {
        //given
        User user = getSavedUser();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        //when
        String refreshToken = jwtService.generateRefreshToken(response, user);

        request.setCookies(new Cookie(REFRESH_PREFIX.getValue(), refreshToken));
        String resolvedToken = jwtService.resolveTokenFromCookie(request, REFRESH_PREFIX);

        //then
        assertThat(refreshToken).isEqualTo(resolvedToken);
    }

    @Test
    @DisplayName("Access-token은 없고 유효한 Refresh-token만 있을 때, Access-token을 추출한다면 \"\"을 반환해야 한다.")
    @WithMockCustomUser
    public void should_returnBlank_whenOnlyValidRefreshToken() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(tokenTestUtil.createRefreshCookie());

        //when
        JwtRule accessTokenPrefix = ACCESS_PREFIX;
        String resolvedToken = jwtService.resolveTokenFromCookie(request, accessTokenPrefix);

        //then
        assertThat(resolvedToken).isEqualTo("");
    }

    @Test
    @DisplayName("Cookie에 아무런 JWT 토큰이 존재하지 않는다면 예외를 발생해야 한다.")
    public void should_throwException_when_noTokens() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();

        //when
        JwtRule refreshTokenPrefix = REFRESH_PREFIX;

        //then
        assertThatThrownBy(() -> jwtService.resolveTokenFromCookie(request, refreshTokenPrefix))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("사용자가 아직 가입하지 않은 회원이 JWT 발급을 요청한다면, 예외를 발생시킨다.")
    public void should_throwException_when_userIsNotRegistered() {
        //given
        User user = getUnregisteredUser();

        //when & then
        assertThatThrownBy(() -> jwtService.validateUser(user))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_AUTHENTICATED_USER.getMessage());
    }

    @Test
    @DisplayName("Refresh-token으로부터 사용자 식별 정보인 Identifier를 받아올 수 있다.")
    @WithMockCustomUser(identifier = "testIdentifier")
    public void should_getIdentifier_when_passRefreshToken() {
        //given
        String refreshToken = tokenTestUtil.createRefreshToken();

        //when
        String identifier = jwtService.getIdentifierFromRefresh(refreshToken);

        //then
        assertThat(identifier).isEqualTo("testIdentifier");
    }

    @ParameterizedTest(name = "Refresh-token: {0}")
    @DisplayName("Refresh-token이 빈 문자열이거나, 유효하지 않은 문자열이라면 예외가 발생해야 한다.")
    @ValueSource(strings = {"invalid refresh token", ""})
    public void should_throwException_when_refreshTokenInvalid(String invalidRefreshToken) {
        //given

        //when&then
        assertThatThrownBy(() -> jwtService.getIdentifierFromRefresh(invalidRefreshToken))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_JWT.getMessage());
    }

    @Test
    @DisplayName("Refresh-token이 유효하지 않는다면 예외가 발생한다.")
    @WithMockCustomUser
    public void should_throwException_when_refreshTokenInvalid() {
        //given
        String refreshToken = "invalid refresh token";

        //when
        assertThatThrownBy(() -> jwtService.validateRefreshToken(refreshToken, "identifier"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(INVALID_JWT.getMessage());
    }


    private User getSavedUser() {
        return userRepository.save(User.builder()
                .providerInfo(ProviderInfo.GITHUB)
                .nickname("nickname")
                .identifier("identifier")
                .role(Role.USER)
                .tags("interest1,interest2")
                .information("information")
                .build());
    }

    private User getUnregisteredUser() {
        return userRepository.save(User.builder()
                .providerInfo(ProviderInfo.GITHUB)
                .identifier("identifier")
                .role(Role.NOT_REGISTERED)
                .build());
    }
}