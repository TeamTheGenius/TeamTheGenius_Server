package com.genius.gitget.challenge.participant.domain;

import lombok.Getter;

@Getter
public enum JoinResult {
    READY,
    PROCESSING,
    FAIL,
    SUCCESS
}
