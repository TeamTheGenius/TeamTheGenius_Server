package com.genius.gitget.challenge.certification.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
class DateUtilTest {

    @Test
    @DisplayName("시작 날짜와 현재 날짜를 전달했을 때, 오늘의 날짜가 몇 번째 회차인지 구할 수 있다.")
    public void should_getAttempt_when_passDate() {
        //given
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 4);

        //when
        int attempt = DateUtil.getCertificationAttempt(startDate, endDate);

        //then
        assertThat(attempt).isEqualTo(33);
    }
}