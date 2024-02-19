package com.genius.gitget.challenge.certification.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class DateUtil {
    public static int getDiffBetweenDate(LocalDate startDate, LocalDate targetDate) {
        return Math.toIntExact(ChronoUnit.DAYS.between(startDate, targetDate)) + 1;
    }

    public static int getWeekAttempt(LocalDate challengeStartDate, LocalDate targetDate) {
        int weekAttempt = targetDate.getDayOfWeek().ordinal() + 1;
        int totalAttempt = getDiffBetweenDate(challengeStartDate, targetDate);

        if ((challengeStartDate.getDayOfWeek() != DayOfWeek.MONDAY) && (totalAttempt < 8)) {
            return totalAttempt;
        }

        return weekAttempt;
    }
}
