package org.dreamexposure.tap.core.objects.blog;

import org.dreamexposure.tap.core.enums.blog.BlogType;
import org.json.JSONObject;

import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/6/18
 * For Project: TAP-Core
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public interface IBlog {
    //Getters
    UUID getBlogId();
    
    String getBaseUrl();
    
    String getCompleteUrl();
    
    BlogType getType();
    
    String getName();
    
    String getDescription();
    
    String getIconUrl();
    
    String getBackgroundColor();
    
    String getBackgroundUrl();
    
    boolean isAllowUnder18();
    
    boolean isNsfw();
    
    //Setters
    void setBlogId(UUID _blogId);
    
    void setBaseUrl(String _baseUrl);
    
    void setCompleteUrl(String _completeUrl);
    
    void setType(BlogType _type);
    
    void setName(String _name);
    
    void setDescription(String _description);
    
    void setIconUrl(String _iconUrl);
    
    void setBackgroundColor(String _backgroundColor);
    
    void setBackgroundUrl(String _backgroundUrl);
    
    void setAllowUnder18(boolean _allowUnder18);
    
    void setNsfw(boolean _nsfw);
    
    JSONObject toJson();
    
    Blog fromJson(JSONObject json);
}
