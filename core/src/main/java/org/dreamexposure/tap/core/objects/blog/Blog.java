package org.dreamexposure.tap.core.objects.blog;

import org.dreamexposure.tap.core.enums.blog.BlogType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/4/2018
 * For Project: TAP-Core
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class Blog implements IBlog {
    private UUID blogId;
    
    private String baseUrl;
    private String completeUrl;
    
    private BlogType type;
    
    private String name;
    private String description;
    
    private String iconUrl;
    private String backgroundColor;
    private String backgroundUrl;
    
    private boolean allowUnder18;
    private boolean nsfw;

    private List<UUID> followers = new ArrayList<>();
    
    //Getters
    public UUID getBlogId() {
        return blogId;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public String getCompleteUrl() {
        return completeUrl;
    }
    
    public BlogType getType() {
        return type;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getIconUrl() {
        return iconUrl;
    }
    
    public String getBackgroundColor() {
        return backgroundColor;
    }
    
    public String getBackgroundUrl() {
        return backgroundUrl;
    }
    
    public boolean isAllowUnder18() {
        return allowUnder18;
    }
    
    public boolean isNsfw() {
        return nsfw;
    }

    public List<UUID> getFollowers() {
        return followers;
    }
    
    //Setters
    public void setBlogId(UUID _blogId) {
        blogId = _blogId;
    }
    
    public void setBaseUrl(String _baseUrl) {
        baseUrl = _baseUrl;
    }
    
    public void setCompleteUrl(String _completeUrl) {
        completeUrl = _completeUrl;
    }
    
    public void setType(BlogType _type) {
        type = _type;
    }
    
    public void setName(String _name) {
        name = _name;
    }
    
    public void setDescription(String _description) {
        description = _description;
    }
    
    public void setIconUrl(String _iconUrl) {
        iconUrl = _iconUrl;
    }
    
    public void setBackgroundColor(String _backgroundColor) {
        backgroundColor = _backgroundColor;
    }
    
    public void setBackgroundUrl(String _backgroundUrl) {
        backgroundUrl = _backgroundUrl;
    }
    
    public void setAllowUnder18(boolean _allowUnder18) {
        allowUnder18 = _allowUnder18;
    }
    
    public void setNsfw(boolean _nsfw) {
        nsfw = _nsfw;
    }
    
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        
        json.put("id", blogId.toString());
        json.put("base_url", baseUrl);
        json.put("complete_url", completeUrl);
        json.put("type", type.name());
        json.put("name", name);
        json.put("description", description);
        json.put("icon_url", iconUrl);
        json.put("background_color", backgroundColor);
        json.put("background_url", backgroundUrl);
        json.put("allow_under_18", allowUnder18);
        json.put("nsfw", nsfw);

        JSONArray jFollowers = new JSONArray();
        for (UUID u : followers) {
            jFollowers.put(u.toString());
        }
        json.put("followers", jFollowers);
        
        return json;
    }
    
    public Blog fromJson(JSONObject json) {
        blogId = UUID.fromString(json.getString("id"));
        baseUrl = json.getString("base_url");
        completeUrl = json.getString("complete_url");
        type = BlogType.valueOf(json.getString("type"));
        name = json.getString("name");
        description = json.getString("description");
        iconUrl = json.getString("icon_url");
        backgroundColor = json.getString("background_color");
        backgroundUrl = json.getString("background_url");
        allowUnder18 = json.getBoolean("allow_under_18");
        nsfw = json.getBoolean("nsfw");

        JSONArray jFollowers = json.getJSONArray("followers");
        for (int i = 0; i < jFollowers.length(); i++) {
            followers.add(UUID.fromString(jFollowers.getString(i)));
        }
        
        return this;
    }
}