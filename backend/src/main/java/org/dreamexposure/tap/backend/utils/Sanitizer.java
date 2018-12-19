package org.dreamexposure.tap.backend.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * @author NovaFox161
 * Date Created: 12/18/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class Sanitizer {

    public static String sanitizeUserInput(String input) {
        String output = input;
        //First we clear out any HTML
        output = Jsoup.clean(output, Whitelist.basic());

        return output;
    }
}
