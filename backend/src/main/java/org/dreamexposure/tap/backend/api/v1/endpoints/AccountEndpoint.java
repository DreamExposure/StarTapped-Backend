package org.dreamexposure.tap.backend.api.v1.endpoints;

import de.triology.recaptchav2java.ReCaptcha;
import org.dreamexposure.novautils.crypto.KeyGenerator;
import org.dreamexposure.tap.backend.conf.GlobalVars;
import org.dreamexposure.tap.backend.conf.SiteSettings;
import org.dreamexposure.tap.backend.network.database.DatabaseHandler;
import org.dreamexposure.tap.backend.network.email.EmailHandler;
import org.dreamexposure.tap.backend.utils.Generator;
import org.dreamexposure.tap.backend.utils.ResponseUtils;
import org.dreamexposure.tap.core.objects.account.Account;
import org.dreamexposure.tap.core.objects.auth.AccountAuthentication;
import org.dreamexposure.tap.core.utils.Logger;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NovaFox161
 * Date Created: 12/5/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping("/api/v1/account")
public class AccountEndpoint {
    @PostMapping(value = "/register", produces = "application/json")
    public static String register(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody) {
        //Skip authentication, user is registering account and does not have auth credentials yet...
        
        
        JSONObject body = new JSONObject(requestBody);
        if (body.has("username") && body.has("email") && body.has("password") && body.has("gcap") && body.has("birthday")) {
            if (new ReCaptcha(SiteSettings.RECAP_KEY.get()).isValid(body.getString("gcap"))) {
                String username = body.getString("username");
                String email = body.getString("email");
                String birthday = body.getString("birthday");
                if (!DatabaseHandler.getHandler().usernameOrEmailTaken(username, email)) {
                    //Generate hash and create account in the database.
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    String hash = encoder.encode(body.getString("password"));
                    
                    DatabaseHandler.getHandler().createAccount(username, email, hash, birthday);
                    Account account = DatabaseHandler.getHandler().getAccountFromEmail(email);
                    
                    //Send confirmation email!!!
                    EmailHandler.getHandler().sendEmailConfirm(email, Generator.generateEmailConfirmationLink(account));
                    
                    //Generate tokens...
                    AccountAuthentication auth = new AccountAuthentication();
                    auth.setAccountId(account.getAccountId());
                    auth.setAccessToken(KeyGenerator.csRandomAlphaNumericString(32));
                    auth.setRefreshToken(KeyGenerator.csRandomAlphaNumericString(32));
                    auth.setExpire(System.currentTimeMillis() + GlobalVars.oneDayMs); //Auth token good for 24 hours, unless manually revoked.
                    
                    Logger.getLogger().api("User registered account: " + account.getUsername(), request.getRemoteAddr());
                    
                    response.setContentType("application/json");
                    response.setStatus(200);
                    
                    JSONObject responseBody = new JSONObject();
                    responseBody.put("message", "Success");
                    responseBody.put("credentials", auth.toJson());
                    
                    return responseBody.toString();
                } else {
                    response.setContentType("application/json");
                    response.setStatus(400);
                    
                    JSONObject responseBody = new JSONObject();
                    responseBody.put("message", "Email/Username Invalid");
                    
                    return responseBody.toString();
                }
            } else {
                response.setContentType("application/json");
                response.setStatus(400);
                
                JSONObject responseBody = new JSONObject();
                responseBody.put("message", "Failed to verify ReCAPTCHA");
                
                return responseBody.toString();
            }
        } else {
            response.setContentType("application/json");
            response.setStatus(400);
            
            JSONObject responseBody = new JSONObject();
            responseBody.put("message", "Bad request");
            
            return responseBody.toString();
        }
    }
    
    @PostMapping(value = "/login", produces = "application/json")
    public static String login(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody) {
        //Skip authentication, user is logging in and does not yet have auth.
        
        JSONObject body = new JSONObject(requestBody);
        if (body.has("email") && body.has("password") && body.has("gcap")) {
            if (new ReCaptcha(SiteSettings.RECAP_KEY.get()).isValid(body.getString("gcap"))) {
                String email = body.getString("email");
                if (DatabaseHandler.getHandler().validLogin(email, body.getString("password"))) {
                    
                    Account account = DatabaseHandler.getHandler().getAccountFromEmail(email);
                    
                    //Generate tokens...
                    AccountAuthentication auth = new AccountAuthentication();
                    auth.setAccountId(account.getAccountId());
                    auth.setAccessToken(KeyGenerator.csRandomAlphaNumericString(32));
                    auth.setRefreshToken(KeyGenerator.csRandomAlphaNumericString(32));
                    auth.setExpire(System.currentTimeMillis() + GlobalVars.oneDayMs); //Auth token good for 24 hours, unless manually revoked.
                    
                    Logger.getLogger().api("User logged into account: " + account.getUsername(), request.getRemoteAddr());
                    
                    response.setContentType("application/json");
                    response.setStatus(200);
                    
                    JSONObject responseBody = new JSONObject();
                    responseBody.put("message", "Success");
                    responseBody.put("credentials", auth.toJson());
                    
                    return responseBody.toString();
                } else {
                    response.setContentType("application/json");
                    response.setStatus(400);
                    
                    JSONObject responseBody = new JSONObject();
                    responseBody.put("message", "Email/Password Invalid");
                    
                    return responseBody.toString();
                }
            } else {
                response.setContentType("application/json");
                response.setStatus(400);
                
                JSONObject responseBody = new JSONObject();
                responseBody.put("message", "Failed to verify ReCAPTCHA");
                
                return responseBody.toString();
            }
        } else {
            response.setContentType("application/json");
            response.setStatus(400);
            
            JSONObject responseBody = new JSONObject();
            responseBody.put("message", "Bad request");
            
            return responseBody.toString();
        }
    }
    
    @PostMapping(value = "/logout", produces = "application/json")
    public static String logout(HttpServletRequest request, HttpServletResponse response) {
        if (request.getHeader("Authorization.Access") != null && request.getHeader("Authorization.Refresh") != null) {
            //User is currently logged in, we can now revoke access and confirm the logout.
            String accessToken = request.getHeader("Authorization.Access");
            String refreshToken = request.getHeader("Authorization.Refresh");
            
            AccountAuthentication auth = DatabaseHandler.getHandler().getAuthFromRefreshToken(refreshToken);
            if (auth == null)
                auth = DatabaseHandler.getHandler().getAuthFromAccessToken(accessToken);
            
            if (auth != null) {
                //Revoke credentials
                DatabaseHandler.getHandler().removeAuthByRefreshToken(auth.getRefreshToken());
                
                response.setContentType("application/json");
                response.setStatus(200);
                
                return ResponseUtils.getJsonResponseMessage("Success");
            } else {
                //Credentials were already revoked, technically this is a success.
                response.setContentType("application/json");
                response.setStatus(200);
                
                return ResponseUtils.getJsonResponseMessage("Success");
            }
        } else {
            //Credentials not provided, cannot revoke credentials if we don't know them.
            response.setContentType("application/json");
            response.setStatus(400);
            
            JSONObject responseBody = new JSONObject();
            responseBody.put("message", "Bad request");
            
            return responseBody.toString();
        }
    }
}
