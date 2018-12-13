package org.dreamexposure.tap.core.objects.cloudflare;

import org.json.JSONObject;

import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/12/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class DnsRecord {
    private UUID blogId;
    private String recordId;
    
    //Getters
    public UUID getBlogId() {
        return blogId;
    }
    
    public String getRecordId() {
        return recordId;
    }
    
    //Setters
    public void setBlogId(UUID _blogId) {
        blogId = _blogId;
    }
    
    public void setRecordId(String _recordId) {
        recordId = _recordId;
    }
    
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("blog_id", blogId.toString());
        json.put("record_id", recordId);
        
        return json;
    }
    
    public DnsRecord fromJson(JSONObject json) {
        blogId = UUID.fromString(json.getString("blog_id"));
        recordId = json.getString("record_id");
        return this;
    }
}
