package org.dreamexposure.tap.backend.utils;

import org.apache.commons.validator.routines.EmailValidator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author NovaFox161
 * Date Created: 12/18/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class Validator {
    public static boolean validEmail(String input) {
        return EmailValidator.getInstance(false).isValid(input);
    }

    public static boolean validPhoneNumber(String input) {

        String scaryAsShitPhoneRegex = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$";
        return input.matches(scaryAsShitPhoneRegex);
    }

    public static boolean validBirthdate(String input) {
        try {
            String DATE_FORMAT = "yyyy-MM-dd";
            Date day = new SimpleDateFormat(DATE_FORMAT).parse(input);
            return day.before(new Date(System.currentTimeMillis()));
        } catch (Exception ignore) {
        }
        return false;
    }

    public static boolean validBlogUrlLength(String input) {
        return input.length() >= 3 && input.length() <= 60;
    }

    public static boolean validColorCode(String input) {
        Pattern colorPattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})");
        Matcher m = colorPattern.matcher(input);
        return m.matches();
    }
}
