package org.dreamexposure.tap.core.conf;

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

    LOG_FOLDER, CREDENTIAL_FOLDER,
    BLOG_FOLDER, TEMPLATE_FOLDER,
    TMP_FOLDER, UPLOAD_FOLDER,

    RECAP_KEY_SITE, RECAP_KEY_ANDROID, RECAP_KEY_IOS,

    SMTP_HOST, SMTP_PORT,
    EMAIL_USER, EMAIL_PASS,
    
    CF_EMAIL, CF_ZONE_ID, CF_AUTH_KEY,

    CLAM_HOST, CLAM_PORT,

    CLAM_NO_SCAN, USE_WEBHOOKS,

    DEBUG_WEBHOOK, ERROR_WEBHOOK, STATUS_WEBHOOK;

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