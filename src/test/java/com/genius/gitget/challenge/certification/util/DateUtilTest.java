package com.genius.gitget.challenge.certification.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Date;
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
        int attempt = DateUtil.getAttemptCount(startDate, endDate);

        //then
        assertThat(attempt).isEqualTo(33);
    }

    @Test
    @DisplayName("첫 주차의 인증 현황을 조회할 때 챌린지의 시작 요일이 월요일이 아니라면, 시작 날짜를 기준으로 계산한다.")
    public void should_calculateByStartDate_when_startDateIsNotMonday() {
        //given
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 3);

        //when
        int weekAttempt = DateUtil.getWeekAttempt(startDate, endDate);

        //then
        assertThat(weekAttempt).isEqualTo(3);
    }

    @Test
    @DisplayName("일반적으로 주차별 인증 현황을 조회할 때, 요일에 따라 계산한다.")
    public void should_calculateByDay_when_getListGenerally() {
        //given
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 15);

        //when
        int weekAttempt = DateUtil.getWeekAttempt(startDate, endDate);

        //then
        assertThat(weekAttempt).isEqualTo(4);
    }

    @Test
    @DisplayName("Date를 전달했을 때 LocalDate로 변환할 수 있다.")
    public void should_convertToLocalDate_when_passDate() {
        //given
        Date date = new Date(1725000000000L);

        //when
        LocalDate localDate = DateUtil.convertToLocalDate(date);

        //then
        assertThat(localDate).isEqualTo(LocalDate.of(2024, 8, 30));
    }

    @Test
    @DisplayName("시작일자과 현재일자를 전달했을 때, 챌린지 시작까지 몇 일 남았는지 구할 수 있다.")
    public void should_getRemainDays_when_passStartDate() {
        //given
        LocalDate startDate = LocalDate.of(2024, 3, 10);
        LocalDate targetDate = LocalDate.of(2024, 3, 1);

        //when
        int remainDays = DateUtil.getRemainDaysToStart(startDate, targetDate);

        //then
        assertThat(remainDays).isEqualTo(9);
    }

    @Test
    @DisplayName("현재일자가 시작일자보다 더 이후의 날짜일 때, 남은 일수를 0으로 반환한다.")
    public void should_returnMinus_when_startDateBeforeThenTargetDate() {
        //given
        LocalDate targetDate = LocalDate.of(2024, 3, 10);
        LocalDate startDate = LocalDate.of(2024, 3, 1);

        //when
        int remainDays = DateUtil.getRemainDaysToStart(startDate, targetDate);

        //then
        assertThat(remainDays).isEqualTo(0);
    }
}