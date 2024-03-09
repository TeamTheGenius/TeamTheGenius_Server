package com.genius.gitget.challenge.certification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CertificateStatus {
    NOT_YET("인증 필요"),
    CERTIFICATED("인증 갱신"),
    PASSED("패스 완료");

    private final String tag;
}
