package org.dreamexposure.tap.backend.conf;

import java.util.Properties;

/**
 * @author NovaFox161
 * Date Created: 12/4/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */

public enum SiteSettings {
    SQL_HOST, SQL_USER,
    SQL_PASSWORD, SQL_DB,
    SQL_PORT, SQL_PREFIX,
    
    REDIS_HOST, REDIS_PORT,
    REDIS_PASS,
    
    TIME_OUT,
    PORT,
    LOG_FOLDER, BLOG_FOLDER,
    RECAP_KEY,
    SMTP_HOST, SMTP_PORT,
    EMAIL_USER, EMAIL_PASS,
    
    CF_EMAIL, CF_ZONE_ID, CF_AUTH_KEY;
    private String val;
    
    SiteSettings() {
    }
    
    public static void init(Properties properties) {
        for (SiteSettings s : values()) {
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