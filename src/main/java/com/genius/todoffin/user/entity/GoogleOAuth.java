package com.genius.todoffin.user.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GoogleOAuth implements SocialOAuth{

    @Value("${spring.security.oath2.client.registration.google.client_id}")
    private String GOOGLE_SNS_CLIENT_ID;

    @Value("${spring.security.oath2.client.registration.google.redirect_uri}")
    private String GOOGLE_SNS_CALLBACK_URL;

    @Value("${spring.security.oath2.client.registration.google.client-secret}")
    private String GOOGLE_SNS_CLIENT_SECRET;

    @Value("${spring.security.oath2.client.registration.google.scope}")
    private String GOOGLE_DATA_ACCESS_SCOPE;

    private final ObjectMapper objectMapper;

    @Override
    public String getOAuthRedirectURL() {

        Map<String, Object> params = new HashMap<>();
        params.put("scope", GOOGLE_DATA_ACCESS_SCOPE);
        params.put("response_type", "code");
        params.put("client_id", GOOGLE_SNS_CLIENT_ID);
        params.put("redirect_uri", GOOGLE_SNS_CALLBACK_URL);

        //parameter를 형식에 맞춰 구성해주는 함수
        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));
        String redirectURL = "https://accounts.google.com/o/oauth2/v2/auth" + "?" + parameterString;
        System.out.println("redirectURL = " + redirectURL);

        return redirectURL;

        /*
         * https://accounts.google.com/o/oauth2/v2/auth?scope=profile&response_type=code
         * &client_id="할당받은 id"&redirect_uri="access token 처리")
         * 로 Redirect URL을 생성하는 로직을 구성
         * */
    }
}
