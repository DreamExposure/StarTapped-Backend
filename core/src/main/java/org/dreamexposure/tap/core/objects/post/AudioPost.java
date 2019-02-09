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
public class AudioPost extends Post {
    private UploadedFile audio;
    
    public AudioPost() {
        setPostType(PostType.AUDIO);
    }
    
    //Getters
    public UploadedFile getAudio() {
        return audio;
    }
    
    //Setters
    public void setAudio(UploadedFile _audio) {
        audio = _audio;
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject base = super.toJson();

        base.put("audio", audio.toJson());
        
        return base;
    }
    
    @Override
    public AudioPost fromJson(JSONObject json) {
        super.fromJson(json);

        audio = new UploadedFile().fromJson(json.getJSONObject("audio"));
        
        return this;
    }
}
