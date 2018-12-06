package org.dreamexposure.tap.backend.utils;

/**
 * @author NovaFox161
 * Date Created: 12/5/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class ResponseUtils {
    public static String getJsonResponseMessage(String msg) {
        return "{\"Message\": \"" + msg + "\"}";
    }
}
