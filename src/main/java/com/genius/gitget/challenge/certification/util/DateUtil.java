package com.genius.gitget.challenge.certification.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

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

        if (isNotStartWithMonday(challengeStartDate, targetDate)) {
            return totalAttempt;
        }

        return weekAttempt;
    }

    public static LocalDate getWeekStartDate(LocalDate challengeStartDate, LocalDate currentDate) {
        if (isNotStartWithMonday(challengeStartDate, currentDate)) {
            return challengeStartDate;
        }

        return currentDate.minusDays(currentDate.getDayOfWeek().ordinal());
    }

    public static LocalDate convertToLocalDate(Date date) {
        return LocalDate.ofInstant(
                date.toInstant(),
                ZoneId.of("Asia/Seoul")
        );
    }

    private static boolean isNotStartWithMonday(LocalDate challengeStartDate, LocalDate currentDate) {
        int totalAttempt = getAttemptCount(challengeStartDate, currentDate);
        // 첫째주이고 && 시작일이 월요일이 아닐 때
        if ((challengeStartDate.getDayOfWeek() != DayOfWeek.MONDAY) && (totalAttempt < 8)) {
            return true;
        }
        return false;
    }
}

