package org.dreamexposure.tap.backend.objects.auth;

import org.dreamexposure.tap.backend.utils.ResponseUtils;
import org.dreamexposure.tap.core.objects.auth.AccountAuthentication;

import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/5/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class AuthenticationState {
    private final UUID id;
    private final boolean success;
    
    private int status;
    
    private String reason;

    private AccountAuthentication auth;
    
    public AuthenticationState(UUID _id, boolean _success) {
        id = _id;
        success = _success;
    }
    
    public UUID getId() {
        return id;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isSuccess() {
        return success;
    }
    
    public int getStatus() {
        return status;
    }
    
    public String getReason() {
        return reason;
    }

    public AccountAuthentication getAuth() {
        return auth;
    }
    
    public AuthenticationState setStatus(int _status) {
        status = _status;
        return this;
    }
    
    public AuthenticationState setReason(String _reason) {
        reason = _reason;
        return this;
    }

    public AuthenticationState setAuth(AccountAuthentication _auth) {
        auth = _auth;
        return this;
    }
    
    public String toJson() {
        return ResponseUtils.getJsonResponseMessage(reason);
    }
}
