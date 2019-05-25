package org.dreamexposure.tap.backend.network.email;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import org.apache.commons.io.FileUtils;
import org.dreamexposure.tap.core.conf.GlobalVars;
import org.dreamexposure.tap.core.conf.SiteSettings;
import org.dreamexposure.tap.core.utils.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * @author NovaFox161
 * Date Created: 12/5/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@Component
public class EmailHandler {
    private static EmailHandler instance;

    private Configuration mailgunConfig;
    
    private EmailHandler() {
    }
    
    public static EmailHandler getHandler() {
        if (instance == null)
            instance = new EmailHandler();
        
        return instance;
    }
    
    public void init() {

        mailgunConfig = new Configuration()
                .domain(SiteSettings.MAILGUN_DOMAIN.get())
                .apiKey(SiteSettings.MAILGUN_API_KEY.get())
                .from("StarTapped | Do Not Reply", "do-not-reply@dreamexposure.org");
    }
    
    public void sendEmailConfirm(String emailTo, String confirmationLink) {
        try {
            String emailHtml = getConfirmEmail(confirmationLink);
            Mail.using(mailgunConfig)
                    .to(emailTo)
                    .subject("Confirm Your Email")
                    .html(emailHtml)
                    .build()
                    .sendAsync();
        } catch (Exception e) {
            Logger.getLogger().exception("Failed to load and send confirmation email", e, true, this.getClass());
        }
    }

    private String getConfirmEmail(String confirmationLink) throws IOException {
        return loadEmailResource("account-confirmation.html")
                .replaceAll("%SITE_URL", GlobalVars.siteUrl)
                .replaceAll("%CONFIRMATION_LINK", confirmationLink)
                .replaceAll("%YEAR", Calendar.getInstance().get(Calendar.YEAR) + "");
    }


    private String loadEmailResource(String fileName) throws IOException {
        return FileUtils.readFileToString(new File(SiteSettings.TEMPLATE_FOLDER.get() + "/email/" + fileName), "UTF-8");
    }
}
