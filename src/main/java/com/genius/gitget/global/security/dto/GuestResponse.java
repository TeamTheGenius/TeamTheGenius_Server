package com.genius.gitget.global.security.dto;

import com.genius.gitget.challenge.user.domain.Role;
import jakarta.validation.constraints.NotNull;

public record GuestResponse(
        String identifier,
        Role role,
        Integer frameId
) {

    public static GuestResponse from(@NotNull AuthResponse authResponse, @NotNull String identifier) {
        return new GuestResponse(identifier, authResponse.role(), authResponse.frameId());
    }
}
