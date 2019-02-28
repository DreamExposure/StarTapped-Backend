package org.dreamexposure.tap.backend.api.v1.endpoints;

import org.dreamexposure.tap.backend.network.auth.Authentication;
import org.dreamexposure.tap.backend.objects.auth.AuthenticationState;
import org.dreamexposure.tap.backend.utils.ResponseUtils;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/auth")
public class AuthEndpoint {
    
    @PostMapping(value = "/refresh", produces = "application/json")
    public static String refresh(HttpServletRequest request, HttpServletResponse response) {
        AuthenticationState authState = Authentication.authenticate(request);
        if (!authState.isSuccess()) {
            response.setStatus(authState.getStatus());
            response.setContentType("application/json");
            return authState.toJson();
        } else {
            if (authState.getStatus() == 201) {
                response.setStatus(201);
                response.setContentType("application/json");

                JSONObject responseBody = new JSONObject();
                responseBody.put("message", authState.getReason());
                responseBody.put("credentials", authState.getAuth().toJson());

                return responseBody.toString();
            } else {
                response.setStatus(200);
                response.setContentType("application/json");

                return ResponseUtils.getJsonResponseMessage(authState.getReason());
            }
        }
    }
}
