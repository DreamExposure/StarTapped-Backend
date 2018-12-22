package org.dreamexposure.tap.backend.api.v1.endpoints;

import org.dreamexposure.tap.backend.conf.GlobalVars;
import org.dreamexposure.tap.backend.network.auth.Authentication;
import org.dreamexposure.tap.backend.network.database.AccountDataHandler;
import org.dreamexposure.tap.backend.network.database.BlogDataHandler;
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
import org.dreamexposure.tap.core.objects.post.AudioPost;
import org.dreamexposure.tap.core.objects.post.ImagePost;
import org.dreamexposure.tap.core.objects.post.TextPost;
import org.dreamexposure.tap.core.objects.post.VideoPost;
import org.dreamexposure.tap.core.utils.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
@RestController
@RequestMapping("/v1/post")
public class PostEndpoint {
    @PostMapping(value = "/create", produces = "application/json")
    public static String register(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody) {
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
                    textPost.setBody(Sanitizer.sanitizeBlogUrl(body.getString("body")));
                    textPost.setNsfw(body.getBoolean("nsfw"));
                    if (body.has("parent"))
                        textPost.setParent(UUID.fromString(body.getString("parent")));
                    else
                        textPost.setParent(null);
                    //TODO: Handle post tags.

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
                    imagePost.setBody(Sanitizer.sanitizeBlogUrl(body.getString("body")));
                    imagePost.setNsfw(body.getBoolean("nsfw"));
                    if (body.has("parent"))
                        imagePost.setParent(UUID.fromString(body.getString("parent")));
                    else
                        imagePost.setParent(null);
                    UploadedFile image = FileUploadHandler.handleBase64Upload(body.getJSONObject("image"), request, authState.getId(), MimeType.IMAGE);
                    if (image != null)
                        imagePost.setImageUrl(image.getUrl());
                    else {
                        //Send failure response...
                        response.setContentType("application/json");
                        response.setStatus(500);

                        return ResponseUtils.getJsonResponseMessage("Failed to handle image upload");
                    }

                    //TODO: Handle post tags.

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
                    audioPost.setBody(Sanitizer.sanitizeBlogUrl(body.getString("body")));
                    audioPost.setNsfw(body.getBoolean("nsfw"));
                    if (body.has("parent"))
                        audioPost.setParent(UUID.fromString(body.getString("parent")));
                    else
                        audioPost.setParent(null);
                    UploadedFile audio = FileUploadHandler.handleBase64Upload(body.getJSONObject("audio"), request, authState.getId(), MimeType.AUDIO);
                    if (audio != null)
                        audioPost.setAudioUrl(audio.getUrl());
                    else {
                        //Send failure response...
                        response.setContentType("application/json");
                        response.setStatus(500);

                        return ResponseUtils.getJsonResponseMessage("Failed to handle audio upload");
                    }

                    //TODO: Handle post tags.

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
                    videoPost.setBody(Sanitizer.sanitizeBlogUrl(body.getString("body")));
                    videoPost.setNsfw(body.getBoolean("nsfw"));
                    if (body.has("parent"))
                        videoPost.setParent(UUID.fromString(body.getString("parent")));
                    else
                        videoPost.setParent(null);
                    UploadedFile video = FileUploadHandler.handleBase64Upload(body.getJSONObject("video"), request, authState.getId(), MimeType.VIDEO);
                    if (video != null)
                        videoPost.setVideoUrl(video.getUrl());
                    else {
                        //Send failure response...
                        response.setContentType("application/json");
                        response.setStatus(500);

                        return ResponseUtils.getJsonResponseMessage("Failed to handle video upload");
                    }

                    //TODO: Handle post tags.

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

            return ResponseUtils.getJsonResponseMessage("Bad Request");
        } catch (Exception e) {
            response.setContentType("application/json");
            response.setStatus(500);

            Logger.getLogger().exception("Failed to handle post create", e, PostEndpoint.class);
            return ResponseUtils.getJsonResponseMessage("Internal Server Error");
        }

    }
}
