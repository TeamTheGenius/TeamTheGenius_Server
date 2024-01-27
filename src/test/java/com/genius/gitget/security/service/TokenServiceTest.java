package com.genius.gitget.security.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.global.security.domain.Token;
import com.genius.gitget.global.security.repository.TokenRepository;
import com.genius.gitget.global.security.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TokenServiceTest {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;

    @Test
    @DisplayName("특정 리프레시 토큰을 identifier를 통해 DB에서 값을 조회할 수 있어야 한다.")
    public void should_findToken_when_findByIdentifier() {
        //given
        String identifier = "SSung023";
        String refreshToken = "refresh token example";
        Token token = Token.builder()
                .identifier(identifier)
                .token(refreshToken)
                .build();

        //when
        Token savedToken = tokenRepository.save(token);
        Token tokenByIdentifier = tokenService.findTokenByIdentifier(identifier);

        //then
        assertThat(savedToken.getIdentifier()).isEqualTo(tokenByIdentifier.getIdentifier());
        assertThat(savedToken.getToken()).isEqualTo(tokenByIdentifier.getToken());

    }

    @Test
    @DisplayName("리프레시 토큰 요청이 들어왔을 때, identifier-token 짝이 맞게 저장되어 있으면 true를 반환한다.")
    public void should_returnTrue_when_tokenValid() {
        //given
        String identifier = "SSung023";
        String refreshToken = "refresh token example";
        Token token = Token.builder()
                .identifier(identifier)
                .token(refreshToken)
                .build();

        //when
        tokenRepository.save(token);
        boolean isRefreshHijacked = tokenService.isRefreshHijacked(identifier, refreshToken);

        //then
        assertThat(isRefreshHijacked).isTrue();
    }

    @Test
    @DisplayName("리프레시 토큰 요청이 들어왔을 때, identifier-token 짝이 맞게 저장되어 있으면 true를 반환한다.")
    public void should_returnFalse_when_tokenInvalid() {
        //given
        String identifier = "SSung023";
        String refreshToken = "refresh token example";
        String fakeRefreshToken = "fake refresh token example";
        Token token = Token.builder()
                .identifier(identifier)
                .token(refreshToken)
                .build();

        //when
        tokenRepository.save(token);
        boolean isRefreshHijacked = tokenService.isRefreshHijacked(identifier, fakeRefreshToken);

        //then
        assertThat(isRefreshHijacked).isFalse();
    }
}