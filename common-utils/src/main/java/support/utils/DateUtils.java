package support.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static String today(String format) {
        return formatDate(LocalDate.now(), format);
    }

    public static String yesterday(String format) {
        return formatDate(LocalDate.now().minusDays(1), format);
    }

    public static String tomorrow(String format) {
        return formatDate(LocalDate.now().plusDays(1), format);
    }

    public static String daysFromToday(int offset, String format) {
        return formatDate(LocalDate.now().plusDays(offset), format);
    }

    public static String currentYear() {
        return String.valueOf(LocalDate.now().getYear());
    }

    public static String nextYear() {
        return String.valueOf(LocalDate.now().plusYears(1).getYear());
    }

    public static String previousYear() {
        return String.valueOf(LocalDate.now().minusYears(1).getYear());
    }

    public static String yearFromNow(int offset) {
        return String.valueOf(LocalDate.now().plusYears(offset).getYear());
    }

    public static String currentMonth() {
        return String.format("%02d", LocalDate.now().getMonthValue());  // 01 to 12
    }

    public static String nextMonth() {
        return String.format("%02d", LocalDate.now().plusMonths(1).getMonthValue());  // 01 to 12
    }

    public static String previousMonth() {
        return String.format("%02d", LocalDate.now().minusMonths(1).getMonthValue());  // 01 to 12
    }

    public static long timestamp() {
        return new Timestamp(System.currentTimeMillis()).getTime();
    }

    public static Date convertDatabaseFormatToDate(String stringDate) throws ParseException {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.UTC);
        LocalDateTime ldt = LocalDateTime.parse(stringDate, fmt);
        Instant instant = ldt.toInstant(ZoneOffset.UTC);
        return Date.from(instant);
    }

    public static Date getDateNextYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        return calendar.getTime();
    }

    public static String getDatabaseUtcDateAndTime() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now().minus(Duration.ofMinutes(2))); // Sanoma organization time is different then DB
    }

    public static long getTimestamp() {
        return new Timestamp(System.currentTimeMillis()).getTime();
    }

    private static String formatDate(LocalDate date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }
}
