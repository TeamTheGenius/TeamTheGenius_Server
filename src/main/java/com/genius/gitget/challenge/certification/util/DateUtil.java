package com.genius.gitget.challenge.certification.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DateUtil {

    public static int getRemainDaysToStart(LocalDate startDate, LocalDate targetDate) {
        if (targetDate.isBefore(startDate)) {
            return (int) ChronoUnit.DAYS.between(targetDate, startDate);
        }
        return 0;
    }

    public static int getAttemptCount(LocalDate startDate, LocalDate targetDate) {
        return Math.toIntExact(ChronoUnit.DAYS.between(startDate, targetDate)) + 1;
    }

    public static int getWeekAttempt(LocalDate challengeStartDate, LocalDate targetDate) {
        int weekAttempt = targetDate.getDayOfWeek().ordinal() + 1;
        int totalAttempt = getAttemptCount(challengeStartDate, targetDate);

        if (isFirstWeek(challengeStartDate, targetDate)) {
            return totalAttempt;
        }

        return weekAttempt;
    }

    public static LocalDate getWeekStartDate(LocalDate challengeStartDate, LocalDate currentDate) {
        if (isFirstWeek(challengeStartDate, currentDate)) {
            return challengeStartDate;
        }
        LocalDate mondayOfWeek = currentDate.minusDays(currentDate.getDayOfWeek().ordinal());
        return mondayOfWeek;
    }

    public static LocalDate convertToKST(Date date) {
        return LocalDate.ofInstant(
                date.toInstant(),
                ZoneId.of("Asia/Seoul")
        );
    }

    public static LocalDate convertToKST(LocalDateTime nowLocal) {
        ZoneId systemZone = ZoneId.systemDefault();
        ZoneId koreaZone = ZoneId.of("Asia/Seoul");

        // 현재 시스템의 ZoneId를 이용하여 ZonedDateTime을 생성
        ZonedDateTime nowZone = ZonedDateTime.of(nowLocal, systemZone);

        // KST(Asia/Seoul)로 변환
        ZonedDateTime koreaTime = nowZone.withZoneSameInstant(koreaZone);

        // LocalDateTime으로 변환하여 반환
        return koreaTime.toLocalDate();
    }

    private static boolean isFirstWeek(LocalDate challengeStartDate, LocalDate currentDate) {
        LocalDate mondayOfWeek = challengeStartDate.minusDays(challengeStartDate.getDayOfWeek().ordinal());
        LocalDate sundayOfWeek = mondayOfWeek.plusDays(6);

        if (currentDate.isAfter(mondayOfWeek.minusDays(1))
                && currentDate.isBefore(sundayOfWeek.plusDays(1))) {
            return true;
        }
        return false;
    }
}

