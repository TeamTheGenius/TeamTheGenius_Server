package com.genius.gitget.security.controller;

import static com.genius.gitget.security.constants.JwtRule.ACCESS_PREFIX;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.genius.gitget.security.domain.UserPrincipal;
import com.genius.gitget.security.service.JwtService;
import com.genius.gitget.user.domain.Role;
import com.genius.gitget.user.domain.User;
import com.genius.gitget.util.WithMockCustomUser;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
@Slf4j
public class AuthControllerTest {
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;

    @Autowired
    JwtService jwtService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("anotation test")
    @WithMockCustomUser(role = Role.USER)
    public void test() throws Exception {
        //given
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userPrincipal.getUser();

        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        String accessCookie = jwtService.generateAccessToken(httpServletResponse, user);
        Cookie cookie = new Cookie(ACCESS_PREFIX.getValue(), accessCookie);

        //when&then
        mockMvc.perform(get("/api/test")
                        .cookie(cookie))
                .andExpect(status().isOk());
    }
}
