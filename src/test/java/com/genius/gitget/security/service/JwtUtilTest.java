package com.genius.gitget.security.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.global.security.constants.JwtRule;
import com.genius.gitget.global.security.service.JwtUtil;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class JwtUtilTest {
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("모든 것이 리셋된 Cookie를 반환받을 수 있다.")
    public void should_returnResetCookie() {
        //given

        //when
        Cookie cookie = jwtUtil.resetToken(JwtRule.ACCESS_PREFIX);

        //then
        assertThat(cookie.getMaxAge()).isEqualTo(0);
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getValue()).isNull();
    }

}