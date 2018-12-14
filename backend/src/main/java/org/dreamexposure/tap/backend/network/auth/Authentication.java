package org.dreamexposure.tap.backend.network.auth;

import org.dreamexposure.novautils.crypto.KeyGenerator;
import org.dreamexposure.tap.backend.conf.GlobalVars;
import org.dreamexposure.tap.backend.network.database.DatabaseHandler;
import org.dreamexposure.tap.backend.objects.auth.AuthenticationState;
import org.dreamexposure.tap.core.objects.auth.AccountAuthentication;
import org.dreamexposure.tap.core.utils.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * @author NovaFox161
 * Date Created: 12/5/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings("Duplicates")
public class Authentication {
    public static AuthenticationState authenticate(HttpServletRequest request) {
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            Logger.getLogger().api("Denied '" + request.getMethod() + "' access", request.getRemoteAddr());
            return new AuthenticationState(null, false).setStatus(405).setReason("Method not allowed");
        }
        //Check authorization
        if (request.getHeader("Authorization_Access") != null) {
            String accessToken = request.getHeader("Authorization_Access");
            
            AccountAuthentication auth = DatabaseHandler.getHandler().getAuthFromAccessToken(accessToken);
            
            if (auth != null) {
                if (auth.getExpire() >= System.currentTimeMillis()) {
                    //Authorized
                    return new AuthenticationState(auth.getAccountId(), true).setStatus(200).setReason("Success");
                } else {
                    //Access code is bad, refresh required.
                    if (request.getHeader("Authorization_Refresh") != null) {
                        String refreshToken = request.getHeader("Authorization_Refresh");
                        auth = DatabaseHandler.getHandler().getAuthFromRefreshToken(refreshToken);
                        
                        if (auth != null) {
                            auth.setAccessToken(KeyGenerator.csRandomAlphaNumericString(32));
                            auth.setExpire(System.currentTimeMillis() + GlobalVars.oneDayMs);
                            DatabaseHandler.getHandler().updateAuth(auth);
    
                            return new AuthenticationState(auth.getAccountId(), true).setStatus(200).setReason("Success. Access token regenerated.");
                        } else {
                            //Token expired. Re log is required.
                            return new AuthenticationState(null, false).setStatus(405).setReason("Token Expired. Re-login is required.");
                        }
                    } else {
                        //No refresh token provided.
                        return new AuthenticationState(auth.getAccountId(), false).setStatus(400).setReason("Refresh Token required.");
                    }
                }
            } else {
                //Check if refresh token exists and is valid...
                if (request.getHeader("Authorization_Refresh") != null) {
                    String refreshToken = request.getHeader("Authorization_Refresh");
                    auth = DatabaseHandler.getHandler().getAuthFromRefreshToken(refreshToken);
                    
                    if (auth != null) {
                        auth.setAccessToken(KeyGenerator.csRandomAlphaNumericString(32));
                        auth.setExpire(System.currentTimeMillis() + GlobalVars.oneDayMs);
                        DatabaseHandler.getHandler().updateAuth(auth);
    
                        return new AuthenticationState(auth.getAccountId(), true).setStatus(200).setReason("Success. Access token regenerated.");
                    } else {
                        //Token expired. Re log is required.
                        return new AuthenticationState(null, false).setStatus(405).setReason("Token Expired. Re-login is required.");
                    }
                } else {
                    //No refresh token provided.
                    return new AuthenticationState(null, false).setStatus(400).setReason("Refresh Token required");
                }
            }
        } else {
            return new AuthenticationState(null, false).setStatus(400).setReason("Access Token required");
        }
    }
}
