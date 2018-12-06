package org.dreamexposure.tap.core.objects.auth;

import org.json.JSONObject;

import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/5/18
 * For Project: TAP-Core
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class AccountAuthentication {
    private UUID accountId;
    
    private String refreshToken;
    private String accessToken;
    
    private long expire;
    
    //Getters
    public UUID getAccountId() {
        return accountId;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public long getExpire() {
        return expire;
    }
    
    //Setters
    public void setAccountId(UUID _id) {
        accountId = _id;
    }
    
    public void setRefeshToken(String _token) {
        refreshToken = _token;
    }
    
    public void setAccessToken(String _token) {
        accessToken = _token;
    }
    
    public void setExpire(long _expire) {
        expire = _expire;
    }
    
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        
        json.put("account-id", accountId.toString());
        json.put("refresh-token", refreshToken);
        json.put("access-token", accessToken);
        json.put("expire", expire);
        
        return json;
    }
    
    public AccountAuthentication fromJson(JSONObject json) {
        accountId = UUID.fromString(json.getString("account-id"));
        refreshToken = json.getString("refresh-token");
        accessToken = json.getString("access-token");
        json.put("expire", expire);
        
        return this;
    }
}
