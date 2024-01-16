package com.genius.todoffin.security.service;

import static com.genius.todoffin.security.constants.JwtRule.ACCESS_PREFIX;
import static com.genius.todoffin.security.constants.JwtRule.REFRESH_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.todoffin.security.constants.ProviderInfo;
import com.genius.todoffin.user.domain.Role;
import com.genius.todoffin.user.domain.User;
import com.genius.todoffin.user.repository.UserRepository;
import com.genius.todoffin.util.exception.BusinessException;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional(readOnly = true)
@Slf4j
@ActiveProfiles({"jwt"})
class JwtServiceTest {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;

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


    private User getSavedUser() {
        return userRepository.save(User.builder()
                .providerInfo(ProviderInfo.GITHUB)
                .nickname("nickname")
                .identifier("identifier")
                .role(Role.USER)
                .interest("interest1,interest2")
                .information("information")
                .build());
    }
}