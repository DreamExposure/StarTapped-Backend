package org.dreamexposure.tap.backend.api.v1.endpoints;

import org.dreamexposure.tap.backend.conf.GlobalVars;
import org.dreamexposure.tap.backend.network.auth.Authentication;
import org.dreamexposure.tap.backend.network.database.AccountDataHandler;
import org.dreamexposure.tap.backend.network.database.BlogDataHandler;
import org.dreamexposure.tap.backend.network.database.FollowerDataHandler;
import org.dreamexposure.tap.backend.network.database.PostDataHandler;
import org.dreamexposure.tap.backend.objects.auth.AuthenticationState;
import org.dreamexposure.tap.backend.utils.FileUploadHandler;
import org.dreamexposure.tap.backend.utils.ResponseUtils;
import org.dreamexposure.tap.backend.utils.Sanitizer;
import org.dreamexposure.tap.core.enums.file.MimeType;
import org.dreamexposure.tap.core.enums.post.PostType;
import org.dreamexposure.tap.core.objects.account.Account;
import org.dreamexposure.tap.core.objects.blog.IBlog;
import org.dreamexposure.tap.core.objects.file.UploadedFile;
import org.dreamexposure.tap.core.objects.post.*;
import org.dreamexposure.tap.core.utils.Logger;
import org.dreamexposure.tap.core.utils.MathsUtils;
import org.dreamexposure.tap.core.utils.PostUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
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
@SuppressWarnings("Duplicates")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/post")
public class PostEndpoint {
    @PostMapping(value = "/create", produces = "application/json")
    public static String create(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody) {
        //Authenticate...
        AuthenticationState authState = Authentication.authenticate(request);
        if (!authState.isSuccess()) {
            response.setStatus(authState.getStatus());
            response.setContentType("application/json");
            return authState.toJson();
        }

        Account account = AccountDataHandler.get().getAccountFromId(authState.getId());
        try {
            JSONObject body = new JSONObject(requestBody);

            IBlog blog = BlogDataHandler.get().getBlog(UUID.fromString(body.getString("blog_id")));

            switch (PostType.valueOf(body.getString("type").toUpperCase())) {
                case TEXT:
                    TextPost textPost = new TextPost();
                    textPost.setId(UUID.randomUUID());
                    textPost.setCreator(account);
                    textPost.setOriginBlog(blog);
                    textPost.setPermaLink(GlobalVars.siteUrl + "/" + blog.getBaseUrl() + "/post/" + textPost.getId().toString());
                    textPost.setFullUrl("https://" + blog.getBaseUrl() + ".startapped.com" + "/post/" + textPost.getId().toString());
                    textPost.setTimestamp(System.currentTimeMillis());
                    textPost.setTitle(Sanitizer.sanitizeUserInput(body.getString("title")));
                    textPost.setBody(Sanitizer.sanitizeUserInput(body.getString("body")));
                    textPost.setNsfw(body.getBoolean("nsfw"));
                    if (body.has("parent"))
                        textPost.setParent(UUID.fromString(body.getString("parent")));
                    else
                        textPost.setParent(null);

                    //Handle tags
                    if (body.has("tags")) {
                        JSONArray jTags = body.getJSONArray("tags");
                        for (int i = 0; i < jTags.length(); i++) {
                            textPost.getTags().add(jTags.getString(i));
                        }
                    }

                    if (PostDataHandler.get().addPost(textPost)) {
                        response.setContentType("application/json");
                        response.setStatus(200);

                        JSONObject responseBody = new JSONObject();
                        responseBody.put("message", "success");
                        responseBody.put("post", textPost.toJson());

                        return responseBody.toString();
                    } else {
                        //Failed to save post...
                        response.setContentType("application/json");
                        response.setStatus(500);

                        return ResponseUtils.getJsonResponseMessage("Failed to save post!");
                    }
                case IMAGE:
                    ImagePost imagePost = new ImagePost();
                    imagePost.setId(UUID.randomUUID());
                    imagePost.setCreator(account);
                    imagePost.setOriginBlog(blog);
                    imagePost.setPermaLink(GlobalVars.siteUrl + "/" + blog.getBaseUrl() + "/post/" + imagePost.getId().toString());
                    imagePost.setFullUrl("https://" + blog.getBaseUrl() + ".startapped.com" + "/post/" + imagePost.getId().toString());
                    imagePost.setTimestamp(System.currentTimeMillis());
                    imagePost.setTitle(Sanitizer.sanitizeUserInput(body.getString("title")));
                    imagePost.setBody(Sanitizer.sanitizeUserInput(body.getString("body")));
                    imagePost.setNsfw(body.getBoolean("nsfw"));
                    if (body.has("parent"))
                        imagePost.setParent(UUID.fromString(body.getString("parent")));
                    else
                        imagePost.setParent(null);
                    UploadedFile image = FileUploadHandler.handleBase64Upload(body.getJSONObject("image"), request, authState.getId(), MimeType.IMAGE);
                    if (image != null)
                        imagePost.setImage(image);
                    else {
                        //Send failure response...
                        response.setContentType("application/json");
                        response.setStatus(500);

                        return ResponseUtils.getJsonResponseMessage("Failed to handle image upload");
                    }

                    //Handle tags
                    if (body.has("tags")) {
                        JSONArray jTags = body.getJSONArray("tags");
                        for (int i = 0; i < jTags.length(); i++) {
                            imagePost.getTags().add(jTags.getString(i));
                        }
                    }

                    if (PostDataHandler.get().addPost(imagePost)) {
                        response.setContentType("application/json");
                        response.setStatus(200);

                        JSONObject responseBody = new JSONObject();
                        responseBody.put("message", "success");
                        responseBody.put("post", imagePost.toJson());

                        return responseBody.toString();
                    } else {
                        //Failed to save post...
                        response.setContentType("application/json");
                        response.setStatus(500);

                        return ResponseUtils.getJsonResponseMessage("Failed to save post!");
                    }
                case AUDIO:
                    AudioPost audioPost = new AudioPost();
                    audioPost.setId(UUID.randomUUID());
                    audioPost.setCreator(account);
                    audioPost.setOriginBlog(blog);
                    audioPost.setPermaLink(GlobalVars.siteUrl + "/" + blog.getBaseUrl() + "/post/" + audioPost.getId().toString());
                    audioPost.setFullUrl("https://" + blog.getBaseUrl() + ".startapped.com" + "/post/" + audioPost.getId().toString());
                    audioPost.setTimestamp(System.currentTimeMillis());
                    audioPost.setTitle(Sanitizer.sanitizeUserInput(body.getString("title")));
                    audioPost.setBody(Sanitizer.sanitizeUserInput(body.getString("body")));
                    audioPost.setNsfw(body.getBoolean("nsfw"));
                    if (body.has("parent"))
                        audioPost.setParent(UUID.fromString(body.getString("parent")));
                    else
                        audioPost.setParent(null);
                    UploadedFile audio = FileUploadHandler.handleBase64Upload(body.getJSONObject("audio"), request, authState.getId(), MimeType.AUDIO);
                    if (audio != null)
                        audioPost.setAudio(audio);
                    else {
                        //Send failure response...
                        response.setContentType("application/json");
                        response.setStatus(500);

                        return ResponseUtils.getJsonResponseMessage("Failed to handle audio upload");
                    }

                    //Handle tags
                    if (body.has("tags")) {
                        JSONArray jTags = body.getJSONArray("tags");
                        for (int i = 0; i < jTags.length(); i++) {
                            audioPost.getTags().add(jTags.getString(i));
                        }
                    }

                    if (PostDataHandler.get().addPost(audioPost)) {
                        response.setContentType("application/json");
                        response.setStatus(200);

                        JSONObject responseBody = new JSONObject();
                        responseBody.put("message", "success");
                        responseBody.put("post", audioPost.toJson());

                        return responseBody.toString();
                    } else {
                        //Failed to save post...
                        response.setContentType("application/json");
                        response.setStatus(500);

                        return ResponseUtils.getJsonResponseMessage("Failed to save post!");
                    }
                case VIDEO:
                    VideoPost videoPost = new VideoPost();
                    videoPost.setId(UUID.randomUUID());
                    videoPost.setCreator(account);
                    videoPost.setOriginBlog(blog);
                    videoPost.setPermaLink(GlobalVars.siteUrl + "/" + blog.getBaseUrl() + "/post/" + videoPost.getId().toString());
                    videoPost.setFullUrl("https://" + blog.getBaseUrl() + ".startapped.com" + "/post/" + videoPost.getId().toString());
                    videoPost.setTimestamp(System.currentTimeMillis());
                    videoPost.setTitle(Sanitizer.sanitizeUserInput(body.getString("title")));
                    videoPost.setBody(Sanitizer.sanitizeUserInput(body.getString("body")));
                    videoPost.setNsfw(body.getBoolean("nsfw"));
                    if (body.has("parent"))
                        videoPost.setParent(UUID.fromString(body.getString("parent")));
                    else
                        videoPost.setParent(null);
                    UploadedFile video = FileUploadHandler.handleBase64Upload(body.getJSONObject("video"), request, authState.getId(), MimeType.VIDEO);
                    if (video != null)
                        videoPost.setVideo(video);
                    else {
                        //Send failure response...
                        response.setContentType("application/json");
                        response.setStatus(500);

                        return ResponseUtils.getJsonResponseMessage("Failed to handle video upload");
                    }

                    //Handle tags
                    if (body.has("tags")) {
                        JSONArray jTags = body.getJSONArray("tags");
                        for (int i = 0; i < jTags.length(); i++) {
                            videoPost.getTags().add(jTags.getString(i));
                        }
                    }

                    if (PostDataHandler.get().addPost(videoPost)) {
                        response.setContentType("application/json");
                        response.setStatus(200);

                        JSONObject responseBody = new JSONObject();
                        responseBody.put("message", "success");
                        responseBody.put("post", video.toJson());

                        return responseBody.toString();
                    } else {
                        //Failed to save post...
                        response.setContentType("application/json");
                        response.setStatus(500);

                        return ResponseUtils.getJsonResponseMessage("Failed to save post!");
                    }
                default:
                    //Invalid type
                    response.setContentType("application/json");
                    response.setStatus(400);

                    return ResponseUtils.getJsonResponseMessage("Bad Request");
            }
        } catch (JSONException | IllegalArgumentException e) {
            response.setContentType("application/json");
            response.setStatus(400);

            Logger.getLogger().exception("Possible JSON error in post create", e, PostEndpoint.class);

            return ResponseUtils.getJsonResponseMessage("Bad Request");
        } catch (Exception e) {
            response.setContentType("application/json");
            response.setStatus(500);

            Logger.getLogger().exception("Failed to handle post create", e, PostEndpoint.class);
            return ResponseUtils.getJsonResponseMessage("Internal Server Error");
        }

    }

    @PostMapping(value = "/get/blog", produces = "application/json")
    public static String getByBlog(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody) {
        //Authenticate...
        AuthenticationState authState = Authentication.authenticate(request);
        if (!authState.isSuccess()) {
            response.setStatus(authState.getStatus());
            response.setContentType("application/json");
            return authState.toJson();
        }


        try {
            JSONObject body = new JSONObject(requestBody);
            UUID blogId = UUID.fromString(body.getString("blog_id"));
            int limit = body.getInt("limit");
            long before = body.getLong("before");
            List<String> tags = new ArrayList<>();
            if (body.has("filters")) {
                JSONArray jTags = body.getJSONArray("filters");
                for (int i = 0; i < jTags.length(); i++) {
                    tags.add(jTags.getString(i));
                }
            }

            //Limit must be between 1 and 20 (inclusive)
            if (limit < 1 || limit > 20) {
                //Invalid month specified...
                response.setContentType("application/json");
                response.setStatus(400);

                return ResponseUtils.getJsonResponseMessage("Bad Request");
            }

            if (before <= GlobalVars.starTappedEpoch) {
                //StarTapped epoch hit. No posts could be created before then.
                response.setContentType("application/json");
                response.setStatus(417); //Using this code to signify Epoch hit for clients.

                return ResponseUtils.getJsonResponseMessage("StarTapped Epoch hit. No further posts can be retrieved.");
            }

            //Check if user is minor or has safe search on and blog is NSFW and/or is adults only.
            Account acc = AccountDataHandler.get().getAccountFromId(authState.getId());
            IBlog blog = BlogDataHandler.get().getBlog(blogId);
            if (acc.isSafeSearch() && blog.isNsfw()) {
                response.setContentType("application/json");
                response.setStatus(403);

                return ResponseUtils.getJsonResponseMessage("This blog is marked as NSFW. Disable safe search in order to view posts by this blog.");
            } else if (!blog.isAllowUnder18() && MathsUtils.determineAge(acc.getBirthday()) < 18) {
                response.setContentType("application/json");
                response.setStatus(403);

                return ResponseUtils.getJsonResponseMessage("This blog does not allow minors to view its content. Please come back when you are at least 18 years old.");
            }

            //Get from database...
            List<IPost> posts = PostDataHandler.get().getPostsByBlog(blogId, before, limit, tags);

            //Get our upper and lower times...
            Collections.sort(posts);

            JSONObject range = new JSONObject();
            if (!posts.isEmpty()) {
                range.put("latest", posts.get(0).getTimestamp());
                range.put("oldest", posts.get(posts.size() - 1).getTimestamp());
            } else {
                range.put("latest", 0);
                range.put("oldest", 0);
            }

            //Get all of the parents
            List<IPost> toAdd = new ArrayList<>();

            for (IPost p : posts) {
                List<IPost> parentTree = PostDataHandler.get().getParentTree(p);
                for (IPost pa : parentTree) {
                    if (PostUtils.doesNotHavePost(posts, pa.getId()) && PostUtils.doesNotHavePost(toAdd, pa.getId())) {
                        toAdd.add(pa);
                    }
                }
            }

            posts.addAll(toAdd);


            //Okay, now handle the JSON
            Collections.sort(posts);

            JSONObject responseBody = new JSONObject();
            responseBody.put("message", "Success");

            JSONArray jPosts = new JSONArray();
            for (IPost p : posts) {
                jPosts.put(p.toJson());
            }
            responseBody.put("posts", jPosts);
            responseBody.put("count", jPosts.length());
            responseBody.put("range", range);

            //Respond to client.
            response.setContentType("application/json");
            response.setStatus(200);

            return responseBody.toString();

        } catch (JSONException | IllegalArgumentException e) {
            response.setContentType("application/json");
            response.setStatus(400);

            return ResponseUtils.getJsonResponseMessage("Bad Request");
        } catch (Exception e) {
            response.setContentType("application/json");
            response.setStatus(500);

            Logger.getLogger().exception("Failed to handle post get by blog", e, PostEndpoint.class);
            return ResponseUtils.getJsonResponseMessage("Internal Server Error");
        }
    }

    @PostMapping(value = "/get/search", produces = "application/json")
    public static String getForSearch(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody) {
        //Authenticate...
        AuthenticationState authState = Authentication.authenticate(request);
        if (!authState.isSuccess()) {
            response.setStatus(authState.getStatus());
            response.setContentType("application/json");
            return authState.toJson();
        }


        try {
            JSONObject body = new JSONObject(requestBody);
            int limit = body.getInt("limit");
            long before = body.getLong("before");
            List<String> tags = new ArrayList<>();
            if (body.has("filters")) {
                JSONArray jTags = body.getJSONArray("filters");
                for (int i = 0; i < jTags.length(); i++) {
                    tags.add(jTags.getString(i));
                }
            }

            //Limit must be between 1 and 20 (inclusive)
            if (limit < 1 || limit > 20) {
                //Invalid month specified...
                response.setContentType("application/json");
                response.setStatus(400);

                return ResponseUtils.getJsonResponseMessage("Bad Request");
            }

            if (before <= GlobalVars.starTappedEpoch) {
                //StarTapped epoch hit. No posts could be created before then.
                response.setContentType("application/json");
                response.setStatus(417); //Using this code to signify Epoch hit for clients.

                return ResponseUtils.getJsonResponseMessage("StarTapped Epoch hit. No further posts can be retrieved.");
            }

            //Get from database...
            List<IPost> posts = PostDataHandler.get().getPostsSearch(before, limit, tags);

            //Remove NSFW posts if safe search and/or remove posts from blogs that don't allow minors
            Account acc = AccountDataHandler.get().getAccountFromId(authState.getId());
            int age = MathsUtils.determineAge(acc.getBirthday());

            List<IPost> toRemove = new ArrayList<>();
            for (IPost p : posts) {
                if (p.isNsfw() && acc.isSafeSearch()) {
                    toRemove.add(p);
                } else if (age < 18 && !p.getOriginBlog().isAllowUnder18()) {
                    toRemove.add(p);
                }
            }
            posts.removeAll(toRemove);

            //Get our upper and lower times...
            Collections.sort(posts);

            JSONObject range = new JSONObject();
            if (!posts.isEmpty()) {
                range.put("latest", posts.get(0).getTimestamp());
                range.put("oldest", posts.get(posts.size() - 1).getTimestamp());
            } else {
                range.put("latest", 0);
                range.put("oldest", 0);
            }

            //Get all of the parents
            List<IPost> toAdd = new ArrayList<>();

            for (IPost p : posts) {
                List<IPost> parentTree = PostDataHandler.get().getParentTree(p);
                for (IPost pa : parentTree) {
                    if (PostUtils.doesNotHavePost(posts, pa.getId()) && PostUtils.doesNotHavePost(toAdd, pa.getId())) {
                        toAdd.add(pa);
                    }
                }
            }

            posts.addAll(toAdd);


            //Okay, now handle the JSON
            Collections.sort(posts);

            JSONObject responseBody = new JSONObject();
            responseBody.put("message", "Success");

            JSONArray jPosts = new JSONArray();
            for (IPost p : posts) {
                jPosts.put(p.toJson());
            }
            responseBody.put("posts", jPosts);
            responseBody.put("count", jPosts.length());
            responseBody.put("range", range);

            //Respond to client.
            response.setContentType("application/json");
            response.setStatus(200);

            return responseBody.toString();

        } catch (JSONException | IllegalArgumentException e) {
            response.setContentType("application/json");
            response.setStatus(400);

            return ResponseUtils.getJsonResponseMessage("Bad Request");
        } catch (Exception e) {
            response.setContentType("application/json");
            response.setStatus(500);

            Logger.getLogger().exception("Failed to handle post get by blog", e, PostEndpoint.class);
            return ResponseUtils.getJsonResponseMessage("Internal Server Error");
        }
    }

    @PostMapping(value = "/get/hub", produces = "application/json")
    public static String getForHub(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody) {
        //Authenticate...
        AuthenticationState authState = Authentication.authenticate(request);
        if (!authState.isSuccess()) {
            response.setStatus(authState.getStatus());
            response.setContentType("application/json");
            return authState.toJson();
        }


        try {
            JSONObject body = new JSONObject(requestBody);
            int limit = body.getInt("limit");
            long before = body.getLong("before");
            List<String> tags = new ArrayList<>();
            if (body.has("filters")) {
                JSONArray jTags = body.getJSONArray("filters");
                for (int i = 0; i < jTags.length(); i++) {
                    tags.add(jTags.getString(i));
                }
            }

            //Limit must be between 1 and 20 (inclusive)
            if (limit < 1 || limit > 20) {
                //Invalid month specified...
                response.setContentType("application/json");
                response.setStatus(400);

                return ResponseUtils.getJsonResponseMessage("Bad Request");
            }

            if (before <= GlobalVars.starTappedEpoch) {
                //StarTapped epoch hit. No posts could be created before then.
                response.setContentType("application/json");
                response.setStatus(417); //Using this code to signify Epoch hit for clients.

                return ResponseUtils.getJsonResponseMessage("StarTapped Epoch hit. No further posts can be retrieved.");
            }

            //Get from database...
            List<UUID> following = FollowerDataHandler.get().getFollowingIdList(authState.getId());
            //Add the blogs the user owns to show in the hub too...
            for (IBlog b : BlogDataHandler.get().getBlogs(authState.getId())) {
                following.add(b.getBlogId());
            }
            List<IPost> posts = new ArrayList<>();

            //This gets WAY more than 20 posts, this gets the limit of posts from each blog, we will have to trim this.
            for (UUID bId : following) {
                List<IPost> newPosts = PostDataHandler.get().getPostsByBlog(bId, before, limit, tags);
                for (IPost p : newPosts) {
                    if (PostUtils.doesNotHavePost(posts, p.getId()))
                        posts.add(p);
                }
            }

            Collections.sort(posts);

            //Trim posts...
            if (posts.size() > limit) {
                posts = posts.subList(0, limit);
            }

            //Get our upper and lower times...
            Collections.sort(posts);

            JSONObject range = new JSONObject();
            if (!posts.isEmpty()) {
                range.put("latest", posts.get(0).getTimestamp());
                range.put("oldest", posts.get(posts.size() - 1).getTimestamp());
            } else {
                range.put("latest", 0);
                range.put("oldest", 0);
            }

            //Get all of the parents
            List<IPost> toAdd = new ArrayList<>();

            for (IPost p : posts) {
                List<IPost> parentTree = PostDataHandler.get().getParentTree(p);
                for (IPost pa : parentTree) {
                    if (PostUtils.doesNotHavePost(posts, pa.getId()) && PostUtils.doesNotHavePost(toAdd, pa.getId())) {
                        toAdd.add(pa);
                    }
                }
            }

            posts.addAll(toAdd);

            //Okay, now handle the JSON.
            JSONObject responseBody = new JSONObject();
            responseBody.put("message", "Success");

            JSONArray jPosts = new JSONArray();
            for (IPost p : posts) {
                jPosts.put(p.toJson());
            }
            responseBody.put("posts", jPosts);
            responseBody.put("count", jPosts.length());
            responseBody.put("range", range);

            //Respond to client.
            response.setContentType("application/json");
            response.setStatus(200);

            return responseBody.toString();

        } catch (JSONException | IllegalArgumentException e) {
            response.setContentType("application/json");
            response.setStatus(400);

            Logger.getLogger().exception("Bad Request on post for hub endpoint", e, PostEndpoint.class);

            return ResponseUtils.getJsonResponseMessage("Bad Request");
        } catch (Exception e) {
            response.setContentType("application/json");
            response.setStatus(500);

            Logger.getLogger().exception("Failed to handle post get for hub", e, PostEndpoint.class);
            return ResponseUtils.getJsonResponseMessage("Internal Server Error");
        }
    }

    @PostMapping(value = "/get", produces = "application/json")
    public static String getSingle(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody) {
        //Authenticate...
        AuthenticationState authState = Authentication.authenticate(request);
        if (!authState.isSuccess()) {
            response.setStatus(authState.getStatus());
            response.setContentType("application/json");
            return authState.toJson();
        }


        try {
            JSONObject body = new JSONObject(requestBody);
            UUID postId = UUID.fromString(body.getString("post_id"));

            //Get from database...
            IPost post = PostDataHandler.get().getPost(postId);

            if (post == null) {
                response.setContentType("application/json");
                response.setStatus(404);

                return ResponseUtils.getJsonResponseMessage("Post not Found");
            }

            JSONObject responseBody = new JSONObject();
            responseBody.put("message", "Success");
            responseBody.put("post", post.toJson());

            //Respond to client.
            response.setContentType("application/json");
            response.setStatus(200);

            return responseBody.toString();

        } catch (JSONException | IllegalArgumentException e) {
            response.setContentType("application/json");
            response.setStatus(400);

            return ResponseUtils.getJsonResponseMessage("Bad Request");
        } catch (Exception e) {
            response.setContentType("application/json");
            response.setStatus(500);

            Logger.getLogger().exception("Failed to handle post get single", e, PostEndpoint.class);
            return ResponseUtils.getJsonResponseMessage("Internal Server Error");
        }
    }
}
