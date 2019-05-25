package org.dreamexposure.tap.backend.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
    private static char[] valid = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '_'};

    public static String sanitizeUserInput(String input) {
        Document.OutputSettings settings = new Document.OutputSettings();
        settings.prettyPrint(false);

        return Jsoup.clean(input, "", Whitelist.none(), settings);
    }

    public static String sanitizeBlogUrl(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            for (char c : valid) {
                if (c == input.charAt(i)) {
                    sb.append(input.charAt(i));
                    break;
                }
            }
        }
        String output = sb.toString();

        if (output.startsWith("_") || output.startsWith("-"))
            output = output.substring(1);
        if (output.endsWith("_") || output.endsWith("-"))
            output = output.substring(0, output.length() - 1);

        return output.toLowerCase();
    }
}
