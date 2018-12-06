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
public class VideoPost extends Post {
    private String videoUrl;
    
    public VideoPost() {
        setPostType(PostType.VIDEO);
    }
    
    //Getters
    public String getVideoUrl() {
        return videoUrl;
    }
    
    //Setters
    public void setVideoUrl(String _videoUrl) {
        videoUrl = _videoUrl;
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject base = super.toJson();
        
        base.put("video-url", videoUrl);
        
        return base;
    }
    
    @Override
    public VideoPost fromJson(JSONObject json) {
        super.fromJson(json);
        
        videoUrl = json.getString("video-url");
        
        return this;
    }
}
