package org.dreamexposure.tap.backend.network.email;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import org.dreamexposure.tap.core.conf.GlobalVars;
import org.dreamexposure.tap.core.conf.SiteSettings;

/**
 * @author NovaFox161
 * Date Created: 12/5/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
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
        Mail.using(mailgunConfig)
                .to(emailTo)
                .subject("Confirm Your Email")
                .html(getConfirmEmail(emailTo, confirmationLink))
                .build()
                .sendAsync();
    }
    
    private String getConfirmEmail(String emailTo, String confirmationLink) {
        //TODO: FIX STYLING TO MATCH SITE!!!!!!!!!!!!!!!!!!
        //TODO: Make this actually better and like readable and shit!!!!
        return "<h1 style=\"text-align: center; color: #de1a1a;\">Confirm Your Email</h1>\n" +
                "<p style=\"text-align: center;\">Thank you for signing up for an account at <a title=\"" + GlobalVars.name + "\" href=\"" + GlobalVars.siteUrl + "\" target=\"_blank\">" + GlobalVars.siteUrl + "</a></p>\n" +
                "<p style=\"text-align: center;\">Please click the button below to confirm your email.</p>\n" +
                "<p style=\"text-align: center;\">Didn't sign up? Just ignore this message.</p>\n" +
                "<p style=\"text-align: center;\"><a href=\"" + confirmationLink + "\"><button style=\"font-size: 18px; background-color: #de1a1a; color: white; padding: 10px; border: 2px black; margin: 10px; width: auto; height: auto;\">Confirm Email</button></a></p>\n" +
                "<p style=\"text-align: center;\">&nbsp;Button not working? Click the link below:</p>\n" +
                "<p style=\"text-align: center;\"><a title=\"Confirm Email\" href=\"" + confirmationLink + "\" target=\"_blank\">" + confirmationLink + "</a></p>\n" +
                "<p style=\"text-align: center;\">Email sent to " + emailTo + " in response to account creation</p>\n" +
                "<p style=\"text-align: center;\">&copy; 2018 <a title=\"DreamExposure\" href=\"https://www.dreamexposure.org\" target=\"_blank\">DreamExposure</a> | <a title=\"Privacy Policy\" href=\"https://dreamexposure.org/policy/privacy\" target=\"_blank\">Privacy Policy</a> | <a title=\"Contact\" href=\"https://www.dreamexposure.org/contact\" target=\"_blank\">Contact</a></p>";
    }
}
