package com.genius.gitget.challenge.certification.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @DisplayName("0시부터 14시 59분까지의 Date를 전달했을 때 KST 기준의 LocalDate로 변환할 수 있다.")
    public void should_convertToLocalDate_when_passDate() {
        //given
        //KST = UTC + 9:00
        LocalDateTime utcTime1 = LocalDateTime.of(2024, 6, 9, 0, 0);
        LocalDateTime utcTime2 = LocalDateTime.of(2024, 6, 9, 14, 59);

        Date utcDate1 = Timestamp.valueOf(utcTime1);
        Date utcDate2 = Timestamp.valueOf(utcTime2);

        //when
        LocalDate localDate1 = DateUtil.convertToKST(utcDate1);
        LocalDate localDate2 = DateUtil.convertToKST(utcDate2);

        //then
        assertThat(localDate1).isEqualTo(LocalDate.of(2024, 6, 9));
        assertThat(localDate2).isEqualTo(LocalDate.of(2024, 6, 9));
    }

    @Test
    @DisplayName("UTC 기준 15시 이후의 시간을 KST로 변환하면 다음 날로 인식이 되어야 한다.")
    public void should_convertToKST() {
        // 2024-06-09T23:35:12Z
        LocalDateTime targetDateTime1 = LocalDateTime.of(2024, 6, 9, 15, 0);
        LocalDateTime targetDateTime2 = LocalDateTime.of(2024, 6, 9, 23, 35);

        Date date1 = Timestamp.valueOf(targetDateTime1);
        Date date2 = Timestamp.valueOf(targetDateTime2);

        LocalDate kst1 = DateUtil.convertToKST(date1);
        LocalDate kst2 = DateUtil.convertToKST(date2);

        assertThat(kst1).isEqualTo(LocalDate.of(2024, 6, 10));
        assertThat(kst2).isEqualTo(LocalDate.of(2024, 6, 10));
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

    @Test
    @DisplayName("챌린지 시작 일자가 월요일이 아니고 시작한 그 주일 때, 시작일자를 반환해야 한다.")
    public void should_returnStartDate_when_StartDateNotMonDayAndSecondWeek() {
        LocalDate challengeStartDate = LocalDate.of(2024, 3, 13);
        LocalDate targetDate = LocalDate.of(2024, 3, 15);

        //when
        LocalDate weekStartDate = DateUtil.getWeekStartDate(challengeStartDate, targetDate);

        //then
        assertThat(weekStartDate).isEqualTo(challengeStartDate);
    }

    @Test
    @DisplayName("챌린지 시작일자가 월요일이 아니고 그 다움주일 때, 해당 주의 월요일을 반환해야 한다.")
    public void should_returnMonday_when_startDateIsNotMonday() {
        LocalDate challengeStartDate = LocalDate.of(2024, 3, 10);
        LocalDate targetDate = LocalDate.of(2024, 3, 15);

        //when
        LocalDate weekStartDate = DateUtil.getWeekStartDate(challengeStartDate, targetDate);

        //then

        assertThat(weekStartDate.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
    }

    @Test
    @DisplayName("챌린지 시작일자에 상관없이 시작한지 두 번째 주 일 때, 해당 주의 월요일을 전달해야한다")
    public void should_returnMonday_when_secondWeek() {
        //given
        LocalDate challengeStartDate = LocalDate.of(2024, 3, 10);
        LocalDate targetDate = LocalDate.of(2024, 3, 20);

        //when
        LocalDate weekStartDate = DateUtil.getWeekStartDate(challengeStartDate, targetDate);

        //then
        assertThat(weekStartDate).isEqualTo(LocalDate.of(2024, 3, 18));
        assertThat(weekStartDate.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
    }
}