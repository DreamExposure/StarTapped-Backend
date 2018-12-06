package org.dreamexposure.tap.core.objects.post;

import org.dreamexposure.tap.core.enums.post.PostType;
import org.json.JSONObject;

/**
 * @author NovaFox161
 * Date Created: 12/4/2018
 * For Project: TAP-Core
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class ImagePost extends Post {
    private String imageUrl;
    
    public ImagePost() {
        setPostType(PostType.IMAGE);
    }
    
    //Getters
    public String getImageUrl() {
        return imageUrl;
    }
    
    //Setters
    public void setImageUrl(String _imageUrl) {
        imageUrl = _imageUrl;
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject base = super.toJson();
        
        base.put("image-url", imageUrl);
        
        return base;
    }
    
    @Override
    public ImagePost fromJson(JSONObject json) {
        super.fromJson(json);
        
        imageUrl = json.getString("image-url");
        
        return this;
    }
}
