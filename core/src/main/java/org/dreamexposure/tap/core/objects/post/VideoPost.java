package org.dreamexposure.tap.core.objects.post;

import org.dreamexposure.tap.core.enums.post.PostType;
import org.dreamexposure.tap.core.objects.file.UploadedFile;
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
    private UploadedFile video;
    
    public VideoPost() {
        setPostType(PostType.VIDEO);
    }
    
    //Getters
    public UploadedFile getVideo() {
        return video;
    }
    
    //Setters
    public void setVideo(UploadedFile _video) {
        video = _video;
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject base = super.toJson();

        base.put("video", video.toJson());
        
        return base;
    }
    
    @Override
    public VideoPost fromJson(JSONObject json) {
        super.fromJson(json);

        video = new UploadedFile().fromJson(json.getJSONObject("video"));
        
        return this;
    }
}
