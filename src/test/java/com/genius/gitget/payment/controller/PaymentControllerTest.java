package com.genius.gitget.payment.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genius.gitget.topic.repository.TopicRepository;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.util.TokenTestUtil;
import com.genius.gitget.util.WithMockCustomUser;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;


@SpringBootTest
@Transactional
public class PaymentControllerTest {

    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;
    @Autowired
    TokenTestUtil tokenTestUtil;

    @Autowired
    TopicRepository topicRepository;
    @Autowired
    FilesService filesService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("결제 내역 조회를 요청하면, 상태코드 200을 반환한다.")
    public void 결제_내역_조회_성공() throws Exception {

        mockMvc.perform(get("/api/payment").cookie(tokenTestUtil.createAccessCookie()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    // 토스페이먼츠 PG사 결제 테스트
    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("결제 요청을 성공하면, 상태코드 200을 반환한다.")
    public void 결제_요청_성공() throws Exception {
        Map<String, Object> input = new HashMap<>();
        input.put("amount", 1000L);
        input.put("orderName", "park-kim");
        input.put("pointAmount", 100L);
        input.put("userEmail", "kimdozzi");

        mockMvc.perform(post("/api/payment/toss").cookie(tokenTestUtil.createAccessCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("결제 요청을 실패하면, 상태코드 4xx을 반환한다.")
    public void 결제_요청_실패_1() throws Exception {
        Map<String, Object> input = new HashMap<>();
        input.put("amount", 1000L);
        input.put("orderName", "park-kim");
        input.put("pointAmount", 100L);
        input.put("userEmail", "test@gmail.com");

        mockMvc.perform(post("/api/payment/toss").cookie(tokenTestUtil.createAccessCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("결제 요청을 실패하면, 상태코드 4xx을 반환한다.")
    public void 결제_요청_실패_2() throws Exception {

        mockMvc.perform(post("/api/payment/toss").cookie(tokenTestUtil.createAccessCookie())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("결제 요청을 실패하면, 상태코드 4xx을 반환한다.")
    public void 결제_요청_실패_3() throws Exception {
        Map<String, Object> input = new HashMap<>();
        input.put("amount", 1000L);
        input.put("orderName", "park-kim");
        input.put("pointAmount", 100L);
        input.put("userEmail", "test@gmail.com");

        mockMvc.perform(post("/api/payment/toss").cookie(tokenTestUtil.createAccessCookie())
                        .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
