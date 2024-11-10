package com.genius.gitget.global.security.service;

import static com.genius.gitget.global.util.exception.ErrorCode.JWT_NOT_FOUND_IN_DB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.global.security.domain.Token;
import com.genius.gitget.global.security.repository.TokenRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TokenServiceTest {
    private String identifier = "identifier";
    private String refreshToken = "refresh token example";
    private Token token;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;


    @BeforeEach
    void setup() {
        token = tokenRepository.save(new Token(identifier, refreshToken));
    }

    @AfterEach
    void clearMongo() {
        tokenRepository.deleteAll();
    }

    @Nested
    @DisplayName("DB에 저장되어 있는 Token 객체를 찾으려 할 때")
    class describe_find_stored_token {
        @Nested
        @DisplayName("사용자의 식별자(identifier)를 전달하면")
        class context_pass_identifier {
            @Test
            @DisplayName("저장되어 있던 Token 객체를 반환받을 수 있다.")
            public void it_returns_stored_Token() {
                Token byIdentifier = tokenService.findByIdentifier(identifier);
                assertThat(byIdentifier.getIdentifier()).isEqualTo(identifier);
                assertThat(byIdentifier.getToken()).isEqualTo(refreshToken);
            }
        }
    }

    @Nested
    @DisplayName("Refresh token 탈취 여부를 확인할 때")
    class describe_check_hijack {
        @Nested
        @DisplayName("사용자의 식별자와 요청받은 토큰을 전달하면")
        class context_pass_identifier_and_token {
            @Test
            @DisplayName("저장되어 있던 토큰와 같으면 false를 반환한다.")
            public void it_returns_false_token_same() {
                boolean refreshHijacked = tokenService.isRefreshHijacked(identifier, refreshToken);
                assertThat(refreshHijacked).isFalse();
            }

            @Test
            @DisplayName("저장되어 있던 토큰과 다르다면 true를 반환한다.")
            public void it_returns_true_token_different() {
                String fakeRefreshToken = "fake refresh token";
                tokenRepository.save(new Token(identifier, fakeRefreshToken));

                boolean refreshHijacked = tokenService.isRefreshHijacked(identifier, refreshToken);
                assertThat(refreshHijacked).isTrue();
            }
        }
    }

    @Nested
    @DisplayName("저장되어 있던 토큰을 삭제하고자 할 때")
    class describe_delete_token {
        @Nested
        @DisplayName("사용자의 식별자를 전달하면")
        class context_pass_user_identifier {
            @Test
            @DisplayName("저장되어 있는 토큰을 삭제한다.")
            public void it_delete_stored_token() {
                tokenService.deleteById(identifier);

                assertThatThrownBy(() -> tokenService.findByIdentifier(identifier))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(JWT_NOT_FOUND_IN_DB.getMessage());
            }
        }
    }
}