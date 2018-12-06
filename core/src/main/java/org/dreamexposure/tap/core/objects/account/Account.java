package org.dreamexposure.tap.core.objects.account;

import org.json.JSONObject;

import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/4/2018
 * For Project: TAP-Core
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class Account {
    private UUID accountId;
    
    private String username;
    private String email;
    private String hashedPassword;
    private String phoneNumber;
    private String birthday;
    
    private boolean safeSearch;
    
    private boolean verified;
    private boolean emailConfirmed;
    
    private boolean admin;
    
    //Getters
    public UUID getAccountId() {
        return accountId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getHashedPassword() {
        return hashedPassword;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public String getBirthday() {
        return birthday;
    }
    
    public boolean isSafeSearch() {
        return safeSearch;
    }
    
    public boolean isVerified() {
        return verified;
    }
    
    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }
    
    public boolean isAdmin() {
        return admin;
    }
    
    //Setters
    public void setAccountId(UUID _accountId) {
        accountId = _accountId;
    }
    
    public void setUsername(String _username) {
        username = _username;
    }
    
    public void setEmail(String _email) {
        email = _email;
    }
    
    public void setHashedPassword(String _password) {
        hashedPassword = _password;
    }
    
    public void setPhoneNumber(String _phoneNumber) {
        phoneNumber = _phoneNumber;
    }
    
    public void setBirthday(String _birthday) {
        birthday = _birthday;
    }
    
    public void setSafeSearch(boolean _safeSearch) {
        safeSearch = _safeSearch;
    }
    
    public void setVerified(boolean _verified) {
        verified = _verified;
    }
    
    public void setEmailConfirmed(boolean _confirmed) {
        emailConfirmed = _confirmed;
    }
    
    public void setAdmin(boolean _admin) {
        admin = _admin;
    }
    
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", accountId.toString());
        json.put("username", username);
        json.put("email", email);
        //Skip hashed password.
        json.put("phone-number", phoneNumber);
        json.put("birthday", birthday);
        json.put("safe-search", safeSearch);
        json.put("verified", verified);
        json.put("email-confirmed", emailConfirmed);
        json.put("admin", admin);
        
        return json;
    }
    
    public Account fromJson(JSONObject json) {
        accountId = UUID.fromString(json.getString("id"));
        username = json.getString("username");
        email = json.getString("email");
        //Skip hashed password
        phoneNumber = json.getString("phone-number");
        birthday = json.getString("birthday");
        safeSearch = json.getBoolean("safe-search");
        verified = json.getBoolean("verified");
        emailConfirmed = json.getBoolean("email-confirmed");
        admin = json.getBoolean("admin");
        
        return this;
    }
}