package org.example.credit_processing.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
public class TimeCalculationUtil {

    public static Integer calculateMonths(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(String.format("Invalid arguments startDate: %s, endDate: %s",
                    startDate, endDate));
        }

        long months = ChronoUnit.MONTHS.between(startDate, endDate);

        return (int) Math.max(1, months);
    }

    public static Long calculateDays(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(String.format("Invalid arguments startDate: %s, endDate: %s",
                    startDate, endDate));
        }

        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public static Integer calculateYears(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(String.format("Invalid arguments startDate: %s, endDate: %s",
                    startDate, endDate));
        }

        return (int) ChronoUnit.YEARS.between(startDate, endDate);
    }
}
