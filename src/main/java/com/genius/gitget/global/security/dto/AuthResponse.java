package com.genius.gitget.global.security.dto;

import com.genius.gitget.challenge.user.domain.Role;

public record AuthResponse(
        Role role,
        Long frameItemId
) {
}
