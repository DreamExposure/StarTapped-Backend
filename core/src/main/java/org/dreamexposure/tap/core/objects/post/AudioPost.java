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
public class AudioPost extends Post {
    private String audioUrl;
    
    public AudioPost() {
        setPostType(PostType.AUDIO);
    }
    
    //Getters
    public String getAudioUrl() {
        return audioUrl;
    }
    
    //Setters
    public void setAudioUrl(String _audioUrl) {
        audioUrl = _audioUrl;
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject base = super.toJson();
        
        base.put("audio-url", audioUrl);
        
        return base;
    }
    
    @Override
    public AudioPost fromJson(JSONObject json) {
        super.fromJson(json);
        
        audioUrl = json.getString("audio-url");
        
        return this;
    }
}
