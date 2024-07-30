package com.genius.gitget.global.security.config;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.util.security.TokenTestUtil;
import com.genius.gitget.util.security.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
class SecurityConfigTest {
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;
    @Autowired
    TokenTestUtil tokenTestUtil;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

    }

    @Test
    @DisplayName("swagger에 해당하는 URI에 대해서는 2xx 응답이 발생해야 한다.")
    public void should_status2xx_when_swaggerUri() throws Exception {
        //given
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("permitAll에 해당하지 않는 URI에 대해서는 4xx 응답이 발생해야 한다.")
    public void should_status2xx_when_uriIsPermitAll() throws Exception {
        //given

        //when&then
        mockMvc.perform(get("/api/test"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Admin API에 대해서 Role이 Admin일 때에는 2xx 응답이 발생해야 한다.")
    @WithMockCustomUser(role = Role.ADMIN)
    public void should_status2xx_when_roleIsAdmin() throws Exception {
        //given

        //when & then
        mockMvc.perform(get("/api/admin/topic")
                        .headers(tokenTestUtil.createAccessHeaders()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Admin API에 대해서 Role이 Admin이 아닐 때에는 4xx 응답이 발생해야 한다.")
    @WithMockCustomUser(role = Role.USER)
    public void should_status4xx_when_roleNotAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/topic")
                        .headers(tokenTestUtil.createAccessHeaders()))
                .andExpect(status().is4xxClientError());
    }
}