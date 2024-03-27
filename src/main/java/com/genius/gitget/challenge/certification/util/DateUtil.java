package com.genius.gitget.challenge.certification.util;

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

    public static LocalDate convertToLocalDate(Date date) {
        return LocalDate.ofInstant(
                date.toInstant(),
                ZoneId.of("Asia/Seoul")
        );
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

