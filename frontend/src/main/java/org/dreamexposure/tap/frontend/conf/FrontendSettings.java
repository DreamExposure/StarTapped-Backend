package org.dreamexposure.tap.frontend.conf;

import java.util.Properties;

/**
 * @author NovaFox161
 * Date Created: 12/6/2018
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public enum FrontendSettings {
    TIME_OUT,
    PORT, LOG_FOLDER,
    RECAP_KEY;
    
    private String val;
    
    FrontendSettings() {
    }
    
    public static void init(Properties properties) {
        for (FrontendSettings s : values()) {
            s.set(properties.getProperty(s.name()));
        }
    }
    
    public String get() {
        return val;
    }
    
    public void set(String val) {
        this.val = val;
    }
}