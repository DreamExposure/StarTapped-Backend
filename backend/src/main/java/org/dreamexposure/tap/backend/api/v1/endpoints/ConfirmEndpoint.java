package org.dreamexposure.tap.backend.api.v1.endpoints;

import org.dreamexposure.tap.backend.conf.GlobalVars;
import org.dreamexposure.tap.backend.network.database.AccountDataHandler;
import org.dreamexposure.tap.backend.network.database.ConfirmationDataHandler;
import org.dreamexposure.tap.core.objects.account.Account;
import org.dreamexposure.tap.core.objects.confirmation.EmailConfirmation;
import org.dreamexposure.tap.core.utils.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author NovaFox161
 * Date Created: 12/5/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@RestController
@RequestMapping("confirm")
public class ConfirmEndpoint {
    @GetMapping("/email")
    public static String confirmEmail(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, String> queryParams) {
        if (queryParams.containsKey("code")) {
            String code = queryParams.get("code");
            EmailConfirmation con = ConfirmationDataHandler.get().getConfirmationInfo(code);
            if (con != null) {
                Account account = AccountDataHandler.get().getAccountFromId(con.getUserId());
                account.setEmailConfirmed(true);

                ConfirmationDataHandler.get().removeConfirmationInfo(con.getCode());
                AccountDataHandler.get().updateAccount(account);
                
                Logger.getLogger().api("Confirmed user email: " + account.getAccountId().toString(), request.getRemoteAddr());
                
                //Success... redirect to account page.
                try {
                    response.sendRedirect(GlobalVars.siteUrl + "/account");
                    return "redirect:/account";
                } catch (Exception ignore) {
                    return "redirect:/account";
                }
            } else {
                response.setStatus(400);
                return "Invalid Code!";
            }
        } else {
            response.setStatus(400);
            return "Invalid Request!";
        }
    }
}
