package com.bellamyphan.finora_2026_spring.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

public class DateTimeHandler {

    private DateTimeHandler() {
        // Prevent instantiation
    }

    public static LocalDateTime getCurrentDateWithUpdatedMonth(
            LocalDateTime originalDate,
            LocalDateTime now
    ) {

        YearMonth currentYearMonth = YearMonth.from(now);

        int targetDay = originalDate.getDayOfMonth();

        // Handle invalid dates like Feb 30
        int safeDay = Math.min(
                targetDay,
                currentYearMonth.lengthOfMonth()
        );

        LocalDate newDate = LocalDate.of(
                currentYearMonth.getYear(),
                currentYearMonth.getMonth(),
                safeDay
        );

        return newDate.atTime(originalDate.toLocalTime());
    }
}
