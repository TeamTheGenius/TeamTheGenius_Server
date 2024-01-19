package com.genius.gitget.security.service;

import com.genius.gitget.security.domain.Token;
import com.genius.gitget.security.repository.TokenRepository;
import com.genius.gitget.util.exception.BusinessException;
import com.genius.gitget.util.exception.ErrorCode;
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
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));
    }

    public boolean isRefreshHijacked(String identifier, String refreshToken) {
        Token token = findTokenByIdentifier(identifier);
        return token.getToken().equals(refreshToken);
    }


}
