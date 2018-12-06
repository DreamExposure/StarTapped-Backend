package org.dreamexposure.tap.backend.utils;

import org.dreamexposure.novautils.crypto.KeyGenerator;
import org.dreamexposure.tap.backend.conf.GlobalVars;
import org.dreamexposure.tap.backend.network.database.DatabaseHandler;
import org.dreamexposure.tap.core.objects.account.Account;

/**
 * @author NovaFox161
 * Date Created: 12/5/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class Generator {
    public static String generateEmailConfirmationLink(Account account) {
        String code = KeyGenerator.csRandomAlphaNumericString(32);
        
        //Save to database
        DatabaseHandler.getHandler().addPendingConfirmation(account, code);
        
        return GlobalVars.siteUrl + "/confirm/email?code=" + code;
    }
}
