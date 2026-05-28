package common.util;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    public static String format(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZONE).toLocalDate().format(DATE_FMT);
    }

    public static String formatDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZONE).toLocalDateTime().format(DATETIME_FMT);
    }

    private DateUtil() {}
}
