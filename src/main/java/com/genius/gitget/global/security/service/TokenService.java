package com.genius.gitget.global.security.service;

import com.genius.gitget.global.security.domain.Token;
import com.genius.gitget.global.security.repository.TokenRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public Token findTokenByIdentifier(String identifier) {
        return tokenRepository.findById(identifier)
                .orElseThrow(() -> new BusinessException(ErrorCode.JWT_TOKEN_NOT_FOUND));
    }

    public boolean isRefreshHijacked(String identifier, String refreshToken) {
        Token token = findTokenByIdentifier(identifier);
        return token.getToken().equals(refreshToken);
    }


}
