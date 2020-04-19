package Utils;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeHandler {
    public static String currentDateAsString(String pattern) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat(pattern);
        return formatter.format(new Date());
    }

    public static String currentDayPlus(String format, int additional) {
        Date res = new DateTime(new Date()).plusDays(additional).toDate();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(res);
    }

    public static String currentDayMinutePlus(String format, int additional) {
        Date res = DateUtils.addMinutes(new Date(), additional);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(res);
    }

}
