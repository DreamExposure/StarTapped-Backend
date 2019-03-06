package org.dreamexposure.tap.core.objects.bookmark;

import org.json.JSONObject;

import java.util.UUID;

public class Bookmark {
    private UUID accountId;
    private UUID postId;
    private long timestamp;

    //Getters
    public UUID getAccountId() {
        return accountId;
    }

    public UUID getPostId() {
        return postId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    //Setters
    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    //JSON
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("account_id", accountId.toString());
        json.put("post_id", postId.toString());
        json.put("timestamp", timestamp);

        return json;
    }

    public Bookmark fromJson(JSONObject json) {
        accountId = UUID.fromString(json.getString("account_id"));
        postId = UUID.fromString(json.getString("post_id"));
        timestamp = json.getLong("timestamp");

        return this;
    }
}
