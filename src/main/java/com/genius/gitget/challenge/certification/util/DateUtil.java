package com.genius.gitget.challenge.certification.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class DateUtil {
    public static int getDiffBetweenDate(LocalDate startDate, LocalDate targetDate) {
        return Math.toIntExact(ChronoUnit.DAYS.between(startDate, targetDate)) + 1;
    }
}
