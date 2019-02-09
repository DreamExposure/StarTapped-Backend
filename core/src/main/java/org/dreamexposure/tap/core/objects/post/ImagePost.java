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
public class ImagePost extends Post {
    private UploadedFile image;
    
    public ImagePost() {
        setPostType(PostType.IMAGE);
    }
    
    //Getters
    public UploadedFile getImage() {
        return image;
    }
    
    //Setters
    public void setImage(UploadedFile _image) {
        image = _image;
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject base = super.toJson();

        base.put("image", image.toJson());
        
        return base;
    }
    
    @Override
    public ImagePost fromJson(JSONObject json) {
        super.fromJson(json);

        image = new UploadedFile().fromJson(json.getJSONObject("image"));
        
        return this;
    }
}
