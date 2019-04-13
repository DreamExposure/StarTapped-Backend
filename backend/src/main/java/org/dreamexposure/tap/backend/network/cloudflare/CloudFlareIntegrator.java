package org.dreamexposure.tap.backend.network.cloudflare;

import okhttp3.*;
import org.dreamexposure.tap.backend.network.database.RecordDataHandler;
import org.dreamexposure.tap.core.conf.SiteSettings;
import org.dreamexposure.tap.core.objects.blog.IBlog;
import org.dreamexposure.tap.core.objects.cloudflare.DnsRecord;
import org.dreamexposure.tap.core.utils.Logger;
import org.json.JSONObject;

/**
 * @author NovaFox161
 * Date Created: 12/12/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class CloudFlareIntegrator {
    private static CloudFlareIntegrator instance;
    
    private OkHttpClient client;
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private CloudFlareIntegrator() {
    } //Prevent initialization
    
    public static CloudFlareIntegrator get() {
        if (instance == null)
            instance = new CloudFlareIntegrator();
        
        return instance;
    }
    
    public void init() {
        client = new OkHttpClient();
    }
    
    @SuppressWarnings("ConstantConditions")
    public boolean createCNAMEForBlog(IBlog blog) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("type", "CNAME");
            requestBody.put("name", blog.getBaseUrl());
            requestBody.put("content", "startapped.com");
            requestBody.put("ttl", 1);
            requestBody.put("proxied", true);
            
            RequestBody body = RequestBody.create(JSON, requestBody.toString());
            
            Request httpRequest = new Request.Builder()
                    .url("https://api.cloudflare.com/client/v4/zones/" + SiteSettings.CF_ZONE_ID.get() + "/dns_records")
                    .post(body)
                    .header("X-Auth-Email", SiteSettings.CF_EMAIL.get())
                    .header("X-Auth-Key", SiteSettings.CF_AUTH_KEY.get())
                    .header("Content-Type", "application/json")
                    .build();
            
            Response response = client.newCall(httpRequest).execute();
            
            if (response.code() == 200) {
                //Success
                JSONObject responseBody = new JSONObject(response.body().string());
                
                if (responseBody.getBoolean("success")) {
                    DnsRecord record = new DnsRecord();
                    record.setBlogId(blog.getBlogId());
                    record.setRecordId(responseBody.getJSONObject("result").getString("id"));

                    return RecordDataHandler.get().createRecord(record);
                }
            } else {
                Logger.getLogger().debug("Failed to handle CNAME Creation", response.body().string(), true, this.getClass());
            }
        } catch (Exception e) {
            Logger.getLogger().exception("Failed to create CNAME record", e, true, this.getClass());
        }
        return false;
    }
    
    public boolean deleteCNAMEForBlog(IBlog blog) {
        try {
            DnsRecord record = RecordDataHandler.get().getRecord(blog.getBlogId());
            if (record != null) {
                Request httpRequest = new Request.Builder()
                        .url("https://api.cloudflare.com/client/v4/zones/" + SiteSettings.CF_ZONE_ID.get() + "/dns_records/" + record.getRecordId())
                        .delete()
                        .header("X-Auth-Email", SiteSettings.CF_EMAIL.get())
                        .header("X-Auth-Key", SiteSettings.CF_AUTH_KEY.get())
                        .header("Content-Type", "application/json")
                        .build();
                
                Response response = client.newCall(httpRequest).execute();
                
                if (response.code() == 200) {
                    //Success, delete from database
                    return RecordDataHandler.get().deleteRecord(blog.getBlogId());
                }
            }
        } catch (Exception e) {
            Logger.getLogger().exception("Failed to delete CNAME record", e, true, this.getClass());
        }
        return false;
    }
}