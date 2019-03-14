package org.dreamexposure.tap.core.objects.post;

import org.dreamexposure.tap.core.enums.post.PostType;
import org.dreamexposure.tap.core.objects.account.Account;
import org.dreamexposure.tap.core.objects.blog.Blog;
import org.dreamexposure.tap.core.objects.blog.IBlog;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/4/2018
 * For Project: TAP-Core
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class Post implements IPost, Comparable<IPost> {
    private UUID id;
    private Account creator;
    private IBlog originBlog;
    private String permaLink;
    private String fullUrl;
    private PostType type;
    private long timestamp;
    
    private String title;
    private String body;
    
    private boolean nsfw;

    private UUID parent;
    private boolean bookmarked;

    private List<String> tags = new ArrayList<>();

    private int totalBookmarks;
    private int totalReblogs;
    
    
    //Getters
    public UUID getId() {
        return id;
    }
    
    public Account getCreator() {
        return creator;
    }

    public IBlog getOriginBlog() {
        return originBlog;
    }
    
    public String getPermaLink() {
        return permaLink;
    }
    
    public String getFullUrl() {
        return fullUrl;
    }
    
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public PostType getPostType() {
        return type;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getBody() {
        return body;
    }
    
    public boolean isNsfw() {
        return nsfw;
    }

    @Override
    public UUID getParent() {
        return parent;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public List<String> getTags() {
        return tags;
    }

    @Override
    public String tagsToString() {
        if (tags.isEmpty()) {
            return "";
        } else {
            String tagString = tags.toString();
            return tagString.substring(1, tagString.length() - 1); //This removes leading and trailing []
        }
    }

    @Override
    public int totalBookmarks() {
        return totalBookmarks;
    }

    @Override
    public int totalReblogs() {
        return totalReblogs;
    }

    //Setters
    public void setId(UUID _id) {
        id = _id;
    }
    
    public void setCreator(Account _creator) {
        creator = _creator;
    }

    public void setOriginBlog(IBlog _blog) {
        originBlog = _blog;
    }
    
    public void setPermaLink(String _permaLink) {
        permaLink = _permaLink;
    }
    
    public void setFullUrl(String _fullUrl) {
        fullUrl = _fullUrl;
    }
    
    public void setTimestamp(long _timestamp) {
        timestamp = _timestamp;
    }
    
    public void setPostType(PostType _type) {
        type = _type;
    }
    
    public void setTitle(String _title) {
        title = _title;
    }
    
    public void setBody(String _body) {
        body = _body;
    }
    
    public void setNsfw(boolean _nsfw) {
        nsfw = _nsfw;
    }

    @Override
    public void setParent(UUID _parent) {
        parent = _parent;
    }

    public void setBookmarked(boolean _bookmarked) {
        bookmarked = _bookmarked;
    }

    @Override
    public void tagsFromString(String tagString) {
        tags.addAll(Arrays.asList(tagString.split(",")));
    }

    @Override
    public void setTotalBookmarks(int _bookmarks) {
        totalBookmarks = _bookmarks;
    }

    @Override
    public void setTotalReblogs(int _reblogs) {
        totalReblogs = _reblogs;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        
        json.put("id", id.toString());
        json.put("creator", creator.toJsonNoPersonal());
        json.put("origin_blog", originBlog.toJson());
        json.put("permalink", permaLink);
        json.put("full_url", fullUrl);
        json.put("timestamp", timestamp);
        json.put("type", type.name());
        json.put("title", title);
        json.put("body", body);
        json.put("nsfw", nsfw);
        if (parent != null)
            json.put("parent", parent);
        json.put("bookmarked", bookmarked);

        JSONArray jTags = new JSONArray();
        for (String t : tags) {
            jTags.put(t);
        }
        json.put("tags", jTags);

        json.put("total_bookmarks", totalBookmarks);
        json.put("total_reblogs", totalReblogs);
        
        return json;
    }
    
    public IPost fromJson(JSONObject json) {
        id = UUID.fromString(json.getString("id"));
        creator = new Account().fromJson(json.getJSONObject("creator"));
        originBlog = new Blog().fromJson(json.getJSONObject("origin_blog"));
        permaLink = json.getString("permalink");
        fullUrl = json.getString("full_url");
        timestamp = json.getLong("timestamp");
        type = PostType.valueOf(json.getString("type"));
        title = json.getString("title");
        body = json.getString("body");
        nsfw = json.getBoolean("nsfw");
        if (json.has("parent"))
            parent = UUID.fromString(json.getString("parent"));
        bookmarked = json.getBoolean("bookmarked");

        JSONArray jTags = json.getJSONArray("tags");
        for (int i = 0; i < jTags.length(); i++) {
            tags.add(jTags.getString(i));
        }

        if (json.has("total_bookmarks"))
            totalBookmarks = json.getInt("total_bookmarks");
        if (json.has("total_reblogs"))
            totalReblogs = json.getInt("total_reblogs");

        return this;
    }

    @Override
    public int compareTo(IPost o) {
        return Long.compare(o.getTimestamp(), getTimestamp());
    }
}
