package org.dreamexposure.tap.backend.network.database;

import org.dreamexposure.novautils.database.DatabaseInfo;
import org.dreamexposure.tap.core.enums.post.PostType;
import org.dreamexposure.tap.core.objects.post.*;
import org.dreamexposure.tap.core.utils.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/21/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings({"UnusedReturnValue", "SqlNoDataSourceInspection", "Duplicates"})
public class PostDataHandler {
    private static PostDataHandler instance;

    private DatabaseInfo databaseInfo;

    private String tableName;

    private PostDataHandler() {
    }

    public static PostDataHandler get() {
        if (instance == null) instance = new PostDataHandler();

        return instance;
    }

    void init(DatabaseInfo _info) {
        databaseInfo = _info;
        tableName = String.format("%spost", databaseInfo.getSettings().getPrefix());
    }

    //Getters
    public IPost getPost(UUID postId) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String query = "SELECT * FROM " + tableName + " WHERE id = ?";
                PreparedStatement ps = databaseInfo.getConnection().prepareStatement(query);
                ps.setString(1, postId.toString());

                ResultSet res = ps.executeQuery();

                boolean hasNext = res.next();

                if (hasNext && res.getString("id") != null) {
                    //Data present, let's grab it.
                    switch (PostType.valueOf(res.getString("post_type"))) {
                        case TEXT:
                            TextPost textPost = new TextPost();
                            textPost.setId(postId);
                            textPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            textPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            textPost.setPermaLink(res.getString("permalink"));
                            textPost.setFullUrl(res.getString("full_url"));
                            textPost.setTimestamp(res.getLong("timestamp"));
                            textPost.setTitle(res.getString("title"));
                            textPost.setBody(res.getString("body"));
                            textPost.setNsfw(res.getBoolean("nsfw"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                textPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }


                            ps.close();
                            return textPost;
                        case IMAGE:
                            ImagePost imagePost = new ImagePost();
                            imagePost.setId(postId);
                            imagePost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            imagePost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            imagePost.setPermaLink(res.getString("permalink"));
                            imagePost.setFullUrl(res.getString("full_url"));
                            imagePost.setTimestamp(res.getLong("timestamp"));
                            imagePost.setTitle(res.getString("title"));
                            imagePost.setBody(res.getString("body"));
                            imagePost.setNsfw(res.getBoolean("nsfw"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                imagePost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            imagePost.setImageUrl(res.getString("image_url"));

                            ps.close();
                            return imagePost;
                        case VIDEO:
                            VideoPost videoPost = new VideoPost();
                            videoPost.setId(postId);
                            videoPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            videoPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            videoPost.setPermaLink(res.getString("permalink"));
                            videoPost.setFullUrl(res.getString("full_url"));
                            videoPost.setTimestamp(res.getLong("timestamp"));
                            videoPost.setTitle(res.getString("title"));
                            videoPost.setBody(res.getString("body"));
                            videoPost.setNsfw(res.getBoolean("nsfw"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                videoPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            videoPost.setVideoUrl(res.getString("video_url"));

                            ps.close();
                            return videoPost;
                        case AUDIO:
                            AudioPost audioPost = new AudioPost();
                            audioPost.setId(postId);
                            audioPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            audioPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            audioPost.setPermaLink(res.getString("permalink"));
                            audioPost.setFullUrl(res.getString("full_url"));
                            audioPost.setTimestamp(res.getLong("timestamp"));
                            audioPost.setTitle(res.getString("title"));
                            audioPost.setBody(res.getString("body"));
                            audioPost.setNsfw(res.getBoolean("nsfw"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                audioPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            audioPost.setAudioUrl(res.getString("audio_url"));

                            ps.close();
                            return audioPost;
                    }
                }
                ps.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post", e, this.getClass());
        }
        return null;
    }

    public List<IPost> getPostsByBlog(UUID blogId) {
        List<IPost> posts = new ArrayList<>();
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String query = "SELECT * FROM " + tableName + " WHERE origin_blog_id = ?";
                PreparedStatement ps = databaseInfo.getConnection().prepareStatement(query);
                ps.setString(1, blogId.toString());

                ResultSet res = ps.executeQuery();

                while (res.next()) {
                    if (res.getString("id") != null) {
                        //Data present, let's grab it.
                        switch (PostType.valueOf(res.getString("post_type"))) {
                            case TEXT:
                                TextPost textPost = new TextPost();
                                textPost.setId(UUID.fromString(res.getString("id")));
                                textPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                                textPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                                textPost.setPermaLink(res.getString("permalink"));
                                textPost.setFullUrl(res.getString("full_url"));
                                textPost.setTimestamp(res.getLong("timestamp"));
                                textPost.setTitle(res.getString("title"));
                                textPost.setBody(res.getString("body"));
                                textPost.setNsfw(res.getBoolean("nsfw"));
                                try {
                                    UUID parent = UUID.fromString(res.getString("parent"));
                                    textPost.setParent(parent);
                                } catch (Exception ignore) {
                                    //No parent.
                                }
                                posts.add(textPost);
                                break;
                            case IMAGE:
                                ImagePost imagePost = new ImagePost();
                                imagePost.setId(UUID.fromString(res.getString("id")));
                                imagePost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                                imagePost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                                imagePost.setPermaLink(res.getString("permalink"));
                                imagePost.setFullUrl(res.getString("full_url"));
                                imagePost.setTimestamp(res.getLong("timestamp"));
                                imagePost.setTitle(res.getString("title"));
                                imagePost.setBody(res.getString("body"));
                                imagePost.setNsfw(res.getBoolean("nsfw"));
                                try {
                                    UUID parent = UUID.fromString(res.getString("parent"));
                                    imagePost.setParent(parent);
                                } catch (Exception ignore) {
                                    //No parent.
                                }
                                imagePost.setImageUrl(res.getString("image_url"));

                                posts.add(imagePost);
                                break;
                            case VIDEO:
                                VideoPost videoPost = new VideoPost();
                                videoPost.setId(UUID.fromString(res.getString("id")));
                                videoPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                                videoPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                                videoPost.setPermaLink(res.getString("permalink"));
                                videoPost.setFullUrl(res.getString("full_url"));
                                videoPost.setTimestamp(res.getLong("timestamp"));
                                videoPost.setTitle(res.getString("title"));
                                videoPost.setBody(res.getString("body"));
                                videoPost.setNsfw(res.getBoolean("nsfw"));
                                try {
                                    UUID parent = UUID.fromString(res.getString("parent"));
                                    videoPost.setParent(parent);
                                } catch (Exception ignore) {
                                    //No parent.
                                }
                                videoPost.setVideoUrl(res.getString("video_url"));

                                posts.add(videoPost);
                                break;
                            case AUDIO:
                                AudioPost audioPost = new AudioPost();
                                audioPost.setId(UUID.fromString(res.getString("id")));
                                audioPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                                audioPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                                audioPost.setPermaLink(res.getString("permalink"));
                                audioPost.setFullUrl(res.getString("full_url"));
                                audioPost.setTimestamp(res.getLong("timestamp"));
                                audioPost.setTitle(res.getString("title"));
                                audioPost.setBody(res.getString("body"));
                                audioPost.setNsfw(res.getBoolean("nsfw"));
                                try {
                                    UUID parent = UUID.fromString(res.getString("parent"));
                                    audioPost.setParent(parent);
                                } catch (Exception ignore) {
                                    //No parent.
                                }
                                audioPost.setAudioUrl(res.getString("audio_url"));

                                posts.add(audioPost);
                                break;
                        }
                    }
                    ps.close();
                }
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post", e, this.getClass());
        }
        return posts;
    }

    public List<IPost> getPostsByBlog(UUID blogId, long start, long end) {
        List<IPost> posts = new ArrayList<>();
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String query = "SELECT * FROM " + tableName + " WHERE origin_blog_id = ? AND timestamp BETWEEN ? AND ?";
                PreparedStatement ps = databaseInfo.getConnection().prepareStatement(query);
                ps.setString(1, blogId.toString());
                ps.setLong(2, start);
                ps.setLong(3, end);

                ResultSet res = ps.executeQuery();

                while (res.next()) {
                    if (res.getString("id") != null) {
                        //Data present, let's grab it.
                        switch (PostType.valueOf(res.getString("post_type"))) {
                            case TEXT:
                                TextPost textPost = new TextPost();
                                textPost.setId(UUID.fromString(res.getString("id")));
                                textPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                                textPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                                textPost.setPermaLink(res.getString("permalink"));
                                textPost.setFullUrl(res.getString("full_url"));
                                textPost.setTimestamp(res.getLong("timestamp"));
                                textPost.setTitle(res.getString("title"));
                                textPost.setBody(res.getString("body"));
                                textPost.setNsfw(res.getBoolean("nsfw"));
                                try {
                                    UUID parent = UUID.fromString(res.getString("parent"));
                                    textPost.setParent(parent);
                                } catch (Exception ignore) {
                                    //No parent.
                                }
                                posts.add(textPost);
                                break;
                            case IMAGE:
                                ImagePost imagePost = new ImagePost();
                                imagePost.setId(UUID.fromString(res.getString("id")));
                                imagePost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                                imagePost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                                imagePost.setPermaLink(res.getString("permalink"));
                                imagePost.setFullUrl(res.getString("full_url"));
                                imagePost.setTimestamp(res.getLong("timestamp"));
                                imagePost.setTitle(res.getString("title"));
                                imagePost.setBody(res.getString("body"));
                                imagePost.setNsfw(res.getBoolean("nsfw"));
                                try {
                                    UUID parent = UUID.fromString(res.getString("parent"));
                                    imagePost.setParent(parent);
                                } catch (Exception ignore) {
                                    //No parent.
                                }
                                imagePost.setImageUrl(res.getString("image_url"));

                                posts.add(imagePost);
                                break;
                            case VIDEO:
                                VideoPost videoPost = new VideoPost();
                                videoPost.setId(UUID.fromString(res.getString("id")));
                                videoPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                                videoPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                                videoPost.setPermaLink(res.getString("permalink"));
                                videoPost.setFullUrl(res.getString("full_url"));
                                videoPost.setTimestamp(res.getLong("timestamp"));
                                videoPost.setTitle(res.getString("title"));
                                videoPost.setBody(res.getString("body"));
                                videoPost.setNsfw(res.getBoolean("nsfw"));
                                try {
                                    UUID parent = UUID.fromString(res.getString("parent"));
                                    videoPost.setParent(parent);
                                } catch (Exception ignore) {
                                    //No parent.
                                }
                                videoPost.setVideoUrl(res.getString("video_url"));

                                posts.add(videoPost);
                                break;
                            case AUDIO:
                                AudioPost audioPost = new AudioPost();
                                audioPost.setId(UUID.fromString(res.getString("id")));
                                audioPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                                audioPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                                audioPost.setPermaLink(res.getString("permalink"));
                                audioPost.setFullUrl(res.getString("full_url"));
                                audioPost.setTimestamp(res.getLong("timestamp"));
                                audioPost.setTitle(res.getString("title"));
                                audioPost.setBody(res.getString("body"));
                                audioPost.setNsfw(res.getBoolean("nsfw"));
                                try {
                                    UUID parent = UUID.fromString(res.getString("parent"));
                                    audioPost.setParent(parent);
                                } catch (Exception ignore) {
                                    //No parent.
                                }
                                audioPost.setAudioUrl(res.getString("audio_url"));

                                posts.add(audioPost);
                                break;
                        }
                    }
                    ps.close();
                }
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post", e, this.getClass());
        }
        return posts;
    }

    public List<IPost> getPostsByAccount(UUID accountId) {
        List<IPost> posts = new ArrayList<>();
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String query = "SELECT * FROM " + tableName + " WHERE creator_id = ?";
                PreparedStatement ps = databaseInfo.getConnection().prepareStatement(query);
                ps.setString(1, accountId.toString());

                ResultSet res = ps.executeQuery();

                while (res.next()) {
                    if (res.getString("id") != null) {
                        //Data present, let's grab it.
                        switch (PostType.valueOf(res.getString("post_type"))) {
                            case TEXT:
                                TextPost textPost = new TextPost();
                                textPost.setId(UUID.fromString(res.getString("id")));
                                textPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                                textPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                                textPost.setPermaLink(res.getString("permalink"));
                                textPost.setFullUrl(res.getString("full_url"));
                                textPost.setTimestamp(res.getLong("timestamp"));
                                textPost.setTitle(res.getString("title"));
                                textPost.setBody(res.getString("body"));
                                textPost.setNsfw(res.getBoolean("nsfw"));
                                try {
                                    UUID parent = UUID.fromString(res.getString("parent"));
                                    textPost.setParent(parent);
                                } catch (Exception ignore) {
                                    //No parent.
                                }
                                posts.add(textPost);
                                break;
                            case IMAGE:
                                ImagePost imagePost = new ImagePost();
                                imagePost.setId(UUID.fromString(res.getString("id")));
                                imagePost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                                imagePost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                                imagePost.setPermaLink(res.getString("permalink"));
                                imagePost.setFullUrl(res.getString("full_url"));
                                imagePost.setTimestamp(res.getLong("timestamp"));
                                imagePost.setTitle(res.getString("title"));
                                imagePost.setBody(res.getString("body"));
                                imagePost.setNsfw(res.getBoolean("nsfw"));
                                try {
                                    UUID parent = UUID.fromString(res.getString("parent"));
                                    imagePost.setParent(parent);
                                } catch (Exception ignore) {
                                    //No parent.
                                }
                                imagePost.setImageUrl(res.getString("image_url"));

                                posts.add(imagePost);
                                break;
                            case VIDEO:
                                VideoPost videoPost = new VideoPost();
                                videoPost.setId(UUID.fromString(res.getString("id")));
                                videoPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                                videoPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                                videoPost.setPermaLink(res.getString("permalink"));
                                videoPost.setFullUrl(res.getString("full_url"));
                                videoPost.setTimestamp(res.getLong("timestamp"));
                                videoPost.setTitle(res.getString("title"));
                                videoPost.setBody(res.getString("body"));
                                videoPost.setNsfw(res.getBoolean("nsfw"));
                                try {
                                    UUID parent = UUID.fromString(res.getString("parent"));
                                    videoPost.setParent(parent);
                                } catch (Exception ignore) {
                                    //No parent.
                                }
                                videoPost.setVideoUrl(res.getString("video_url"));

                                posts.add(videoPost);
                                break;
                            case AUDIO:
                                AudioPost audioPost = new AudioPost();
                                audioPost.setId(UUID.fromString(res.getString("id")));
                                audioPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                                audioPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                                audioPost.setPermaLink(res.getString("permalink"));
                                audioPost.setFullUrl(res.getString("full_url"));
                                audioPost.setTimestamp(res.getLong("timestamp"));
                                audioPost.setTitle(res.getString("title"));
                                audioPost.setBody(res.getString("body"));
                                audioPost.setNsfw(res.getBoolean("nsfw"));
                                try {
                                    UUID parent = UUID.fromString(res.getString("parent"));
                                    audioPost.setParent(parent);
                                } catch (Exception ignore) {
                                    //No parent.
                                }
                                audioPost.setAudioUrl(res.getString("audio_url"));

                                posts.add(audioPost);
                                break;
                        }
                    }
                    ps.close();
                }
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post", e, this.getClass());
        }
        return posts;
    }

    public List<IPost> getPostsByAccount(UUID accountId, long start, long end) {
        List<IPost> posts = new ArrayList<>();
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String query = "SELECT * FROM " + tableName + " WHERE creator_id = ? AND timestamp BETWEEN  ? AND ?";
                PreparedStatement ps = databaseInfo.getConnection().prepareStatement(query);
                ps.setString(1, accountId.toString());
                ps.setLong(2, start);
                ps.setLong(3, end);

                ResultSet res = ps.executeQuery();

                while (res.next()) {
                    if (res.getString("id") != null) {
                        //Data present, let's grab it.
                        switch (PostType.valueOf(res.getString("post_type"))) {
                            case TEXT:
                                TextPost textPost = new TextPost();
                                textPost.setId(UUID.fromString(res.getString("id")));
                                textPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                                textPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                                textPost.setPermaLink(res.getString("permalink"));
                                textPost.setFullUrl(res.getString("full_url"));
                                textPost.setTimestamp(res.getLong("timestamp"));
                                textPost.setTitle(res.getString("title"));
                                textPost.setBody(res.getString("body"));
                                textPost.setNsfw(res.getBoolean("nsfw"));
                                try {
                                    UUID parent = UUID.fromString(res.getString("parent"));
                                    textPost.setParent(parent);
                                } catch (Exception ignore) {
                                    //No parent.
                                }
                                posts.add(textPost);
                                break;
                            case IMAGE:
                                ImagePost imagePost = new ImagePost();
                                imagePost.setId(UUID.fromString(res.getString("id")));
                                imagePost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                                imagePost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                                imagePost.setPermaLink(res.getString("permalink"));
                                imagePost.setFullUrl(res.getString("full_url"));
                                imagePost.setTimestamp(res.getLong("timestamp"));
                                imagePost.setTitle(res.getString("title"));
                                imagePost.setBody(res.getString("body"));
                                imagePost.setNsfw(res.getBoolean("nsfw"));
                                try {
                                    UUID parent = UUID.fromString(res.getString("parent"));
                                    imagePost.setParent(parent);
                                } catch (Exception ignore) {
                                    //No parent.
                                }
                                imagePost.setImageUrl(res.getString("image_url"));

                                posts.add(imagePost);
                                break;
                            case VIDEO:
                                VideoPost videoPost = new VideoPost();
                                videoPost.setId(UUID.fromString(res.getString("id")));
                                videoPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                                videoPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                                videoPost.setPermaLink(res.getString("permalink"));
                                videoPost.setFullUrl(res.getString("full_url"));
                                videoPost.setTimestamp(res.getLong("timestamp"));
                                videoPost.setTitle(res.getString("title"));
                                videoPost.setBody(res.getString("body"));
                                videoPost.setNsfw(res.getBoolean("nsfw"));
                                try {
                                    UUID parent = UUID.fromString(res.getString("parent"));
                                    videoPost.setParent(parent);
                                } catch (Exception ignore) {
                                    //No parent.
                                }
                                videoPost.setVideoUrl(res.getString("video_url"));

                                posts.add(videoPost);
                                break;
                            case AUDIO:
                                AudioPost audioPost = new AudioPost();
                                audioPost.setId(UUID.fromString(res.getString("id")));
                                audioPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                                audioPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                                audioPost.setPermaLink(res.getString("permalink"));
                                audioPost.setFullUrl(res.getString("full_url"));
                                audioPost.setTimestamp(res.getLong("timestamp"));
                                audioPost.setTitle(res.getString("title"));
                                audioPost.setBody(res.getString("body"));
                                audioPost.setNsfw(res.getBoolean("nsfw"));
                                try {
                                    UUID parent = UUID.fromString(res.getString("parent"));
                                    audioPost.setParent(parent);
                                } catch (Exception ignore) {
                                    //No parent.
                                }
                                audioPost.setAudioUrl(res.getString("audio_url"));

                                posts.add(audioPost);
                                break;
                        }
                    }
                    ps.close();
                }
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post", e, this.getClass());
        }
        return posts;
    }

    //Setters
    public boolean addPost(IPost post) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String hasDataQuery = "SELECT * FROM " + tableName + " WHERE id = ?";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(hasDataQuery);
                statement.setString(1, post.getId().toString());

                ResultSet res = statement.executeQuery();

                boolean hasData = res.next();

                if (!hasData || res.getString("id") == null) {
                    //No data present, add
                    String insert = "INSERT INTO " + tableName +
                            " (id, creator_id, origin_blog_id, permalink, full_url, post_type, " +
                            " timestamp, title, body, nsfw, parent, " +
                            " image_url, audio_url, video_url)" +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement ps = databaseInfo.getConnection().prepareStatement(insert);

                    ps.setString(1, post.getId().toString());
                    ps.setString(2, post.getCreator().getAccountId().toString());
                    ps.setString(3, post.getOriginBlog().getBlogId().toString());
                    ps.setString(4, post.getPermaLink());
                    ps.setString(5, post.getFullUrl());
                    ps.setString(6, post.getPostType().name());
                    ps.setLong(7, post.getTimestamp());
                    ps.setString(8, post.getTitle());
                    ps.setString(9, post.getBody());
                    ps.setBoolean(10, post.isNsfw());
                    if (post.getParent() != null)
                        ps.setString(11, post.getParent().toString());
                    else
                        ps.setString(11, null);

                    //Specific post type handling
                    switch (post.getPostType()) {
                        case TEXT:
                            ps.setString(12, null);
                            ps.setString(13, null);
                            ps.setString(14, null);
                            break;
                        case IMAGE:
                            ps.setString(12, ((ImagePost) post).getImageUrl());
                            ps.setString(13, null);
                            ps.setString(14, null);
                            break;
                        case AUDIO:
                            ps.setString(12, null);
                            ps.setString(13, ((AudioPost) post).getAudioUrl());
                            ps.setString(14, null);
                            break;
                        case VIDEO:
                            ps.setString(12, null);
                            ps.setString(13, null);
                            ps.setString(14, ((VideoPost) post).getVideoUrl());
                            break;
                        default:
                            ps.setString(12, null);
                            ps.setString(13, null);
                            ps.setString(14, null);
                            break;
                    }

                    ps.execute();
                    ps.close();
                    statement.close();
                    return true;
                } else {
                    //Data present, update
                    String update = "UPDATE " + tableName + " SET " +
                            "creator_id = ?, origin_blog_id = ?, permalink = ?, full_url = ?, post_type = ?, " +
                            "timestamp = ?, title = ?, body = ?, nsfw = ?, parent = ?, " +
                            " image_url = ?, audio_url = ?, video_url = ? " +
                            " WHERE id = ?";
                    PreparedStatement ps = databaseInfo.getConnection().prepareStatement(update);

                    ps.setString(1, post.getCreator().getAccountId().toString());
                    ps.setString(2, post.getOriginBlog().getBlogId().toString());
                    ps.setString(3, post.getPermaLink());
                    ps.setString(4, post.getFullUrl());
                    ps.setString(5, post.getPostType().name());
                    ps.setLong(6, post.getTimestamp());
                    ps.setString(7, post.getTitle());
                    ps.setString(8, post.getBody());
                    ps.setBoolean(9, post.isNsfw());
                    if (post.getParent() != null)
                        ps.setString(10, post.getParent().toString());
                    else
                        ps.setString(10, null);

                    //Specific post type handling
                    switch (post.getPostType()) {
                        case TEXT:
                            ps.setString(11, null);
                            ps.setString(12, null);
                            ps.setString(13, null);
                            break;
                        case IMAGE:
                            ps.setString(11, ((ImagePost) post).getImageUrl());
                            ps.setString(12, null);
                            ps.setString(13, null);
                            break;
                        case AUDIO:
                            ps.setString(11, null);
                            ps.setString(12, ((AudioPost) post).getAudioUrl());
                            ps.setString(13, null);
                            break;
                        case VIDEO:
                            ps.setString(11, null);
                            ps.setString(12, null);
                            ps.setString(13, ((VideoPost) post).getVideoUrl());
                            break;
                        default:
                            ps.setString(11, null);
                            ps.setString(12, null);
                            ps.setString(13, null);
                            break;
                    }
                    ps.setString(14, post.getId().toString());

                    ps.execute();
                    ps.close();
                    statement.close();
                    return true;
                }
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to save post", e, this.getClass());
        }
        return false;
    }
}