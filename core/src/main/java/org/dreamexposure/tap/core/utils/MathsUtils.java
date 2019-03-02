package org.dreamexposure.tap.core.utils;

import org.joda.time.LocalDate;
import org.joda.time.Years;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MathsUtils {
    public static int determineAge(String birthday) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = sdf.parse(birthday);

            LocalDate birth = new LocalDate(date);
            LocalDate now = new LocalDate();
            Years age = Years.yearsBetween(birth, now);

            return age.getYears();
        } catch (ParseException ignore) {
        }
        return -1;
    }
}
