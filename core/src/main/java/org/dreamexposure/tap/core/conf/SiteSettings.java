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
    MASTER_SQL_HOST, MASTER_SQL_PORT,
    MASTER_SQL_USER, MASTER_SQL_PASS,
    MASTER_SQL_USE_SSH,

    SLAVE_SQL_HOST, SLAVE_SQL_PORT,
    SLAVE_SQL_USER, SLAVE_SQL_PASSWORD,

    SQL_DB, SQL_PREFIX,

    SSH_HOST, SSH_PORT,
    SSH_USER, SSH_KEY_FILE,
    
    REDIS_HOST, REDIS_PORT,
    REDIS_PASS,
    
    TIME_OUT,
    PORT,

    LOG_FOLDER, CREDENTIAL_FOLDER,
    BLOG_FOLDER, TEMPLATE_FOLDER,
    TMP_FOLDER, UPLOAD_FOLDER,

    RECAP_KEY_SITE, RECAP_KEY_ANDROID, RECAP_KEY_IOS,

    MAILGUN_DOMAIN, MAILGUN_API_KEY,
    
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