package com.genius.gitget.challenge.instance.dto.search;

import lombok.Builder;

@Builder
public record InstanceSearchRequest(
        String keyword,
        String progress) {
}