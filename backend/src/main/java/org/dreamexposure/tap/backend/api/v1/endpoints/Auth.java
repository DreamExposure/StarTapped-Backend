package org.dreamexposure.tap.backend.api.v1.endpoints;

import org.dreamexposure.novautils.crypto.KeyGenerator;
import org.dreamexposure.tap.backend.conf.GlobalVars;
import org.dreamexposure.tap.backend.network.database.DatabaseHandler;
import org.dreamexposure.tap.backend.utils.ResponseUtils;
import org.dreamexposure.tap.core.objects.auth.AccountAuthentication;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NovaFox161
 * Date Created: 12/13/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping("/v1/auth")
public class Auth {
    
    @PostMapping(value = "/refresh", produces = "application/json")
    public static String refresh(HttpServletRequest request, HttpServletResponse response) {
        if (request.getHeader("Authorization_Refresh") != null && request.getHeader("Authorization_Access") != null) {
            //Lets validate and return...
            String refresh = request.getHeader("Authorization_Refresh");
            
            AccountAuthentication auth = DatabaseHandler.getHandler().getAuthFromRefreshToken(refresh);
            
            if (auth != null) {
                if (auth.getExpire() >= System.currentTimeMillis()) {
                    //No need to refresh, key is still valid
                    response.setStatus(201);
                    response.setContentType("application/json");
                    
                    return ResponseUtils.getJsonResponseMessage("Token still valid. No refresh needed.");
                } else {
                    auth.setAccessToken(KeyGenerator.csRandomAlphaNumericString(32));
                    auth.setExpire(System.currentTimeMillis() + GlobalVars.oneDayMs);
                    DatabaseHandler.getHandler().updateAuth(auth);
                    
                    response.setStatus(200);
                    response.setContentType("application/json");
                    
                    JSONObject responseBody = new JSONObject();
                    responseBody.put("message", "Success");
                    responseBody.put("credentials", auth.toJson());
                    
                    return responseBody.toString();
                }
            } else {
                response.setStatus(405);
                response.setContentType("application/json");
                
                return ResponseUtils.getJsonResponseMessage("Invalid Token(s). Re-log is required.");
            }
        } else {
            response.setStatus(400);
            response.setContentType("application/json");
            
            return ResponseUtils.getJsonResponseMessage("Authorization headers must be provided");
        }
    }
}
