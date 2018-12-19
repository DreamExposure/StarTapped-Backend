package org.dreamexposure.tap.backend.utils;

import org.apache.commons.validator.routines.EmailValidator;

import java.text.SimpleDateFormat;
import java.util.Date;

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
}
