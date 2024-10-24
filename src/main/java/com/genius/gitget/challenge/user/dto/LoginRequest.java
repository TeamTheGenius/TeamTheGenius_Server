package com.genius.gitget.challenge.user.dto;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(
        @NotEmpty
        String id,
        @NotEmpty
        String password
) {
}
