package org.dreamexposure.tap.backend.api.v1.endpoints;

import org.dreamexposure.tap.backend.conf.GlobalVars;
import org.dreamexposure.tap.backend.network.auth.Authentication;
import org.dreamexposure.tap.backend.network.cloudflare.CloudFlareIntegrator;
import org.dreamexposure.tap.backend.network.database.AccountDataHandler;
import org.dreamexposure.tap.backend.network.database.BlogDataHandler;
import org.dreamexposure.tap.backend.objects.auth.AuthenticationState;
import org.dreamexposure.tap.backend.utils.*;
import org.dreamexposure.tap.core.enums.blog.BlogType;
import org.dreamexposure.tap.core.enums.file.MimeType;
import org.dreamexposure.tap.core.objects.account.Account;
import org.dreamexposure.tap.core.objects.blog.GroupBlog;
import org.dreamexposure.tap.core.objects.blog.IBlog;
import org.dreamexposure.tap.core.objects.blog.PersonalBlog;
import org.dreamexposure.tap.core.objects.file.UploadedFile;
import org.dreamexposure.tap.core.utils.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/10/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping("/v1/blog")
public class BlogEndpoint {
    
    @PostMapping(value = "/create", produces = "application/json")
    public static String create(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody) {
        //Authenticate...
        AuthenticationState authState = Authentication.authenticate(request);
        if (!authState.isSuccess()) {
            response.setStatus(authState.getStatus());
            response.setContentType("application/json");
            return authState.toJson();
        }
        //TODO: Handle reCAPTCHA to stop bots....
        
        //Okay, now handle actual request.
        Account account = AccountDataHandler.get().getAccountFromId(authState.getId());
        
        try {
            JSONObject body = new JSONObject(requestBody);
            //Lets get all those variables
            String url = Sanitizer.sanitizeBlogUrl(body.getString("url"));
            BlogType type = BlogType.valueOf(body.getString("type"));
            String name = account.getUsername() + "'s blog";
            String description = "Lorem Ipsum";

            //Valid URL?
            if (!Validator.validBlogUrlLength(url)) {
                response.setContentType("application/json");
                response.setStatus(400);
                return ResponseUtils.getJsonResponseMessage("Invalid Blog URL");
            }
            
            //Check if URL is taken...
            if (BlogDataHandler.get().blogUrlTaken(url)) {
                response.setContentType("application/json");
                response.setStatus(409);
                
                JSONObject responseBody = new JSONObject();
                responseBody.put("message", "Conflict: Blog URL Taken");
                responseBody.put("reason", "Blog URL taken");
                
                return responseBody.toString();
            } else {
                //Create blog!
                if (type == BlogType.PERSONAL) {
                    PersonalBlog blog = new PersonalBlog();
                    blog.setBlogId(UUID.randomUUID());
                    blog.setBaseUrl(url);
                    blog.setCompleteUrl("https://" + url + ".startapped.com");
                    blog.setOwnerId(account.getAccountId());
                    blog.setName(name);
                    blog.setDescription(description);
                    blog.setDisplayAge(true);
                    blog.setNsfw(false);
                    blog.setAllowUnder18(true);
                    
                    //set default images and colors
                    blog.setIconUrl(GlobalVars.cdnUrl + "/img/default/profile.jpg");
                    blog.setBackgroundUrl(GlobalVars.cdnUrl + "/img/default/background.jpg");
                    blog.setBackgroundColor("#ffffff");

                    BlogDataHandler.get().createOrUpdateBlog(blog);
    
                    //Lets get that CNAME record created...
                    if (CloudFlareIntegrator.get().createCNAMEForBlog(blog)) {
        
                        //Create the folders... that's super important
                        if (FileHandler.createDefaultBlogFolders(blog)) {
                            //Respond to client
                            response.setContentType("application/json");
                            response.setStatus(200);
            
                            JSONObject responseBody = new JSONObject();
                            responseBody.put("message", "Success");
                            responseBody.put("blog", blog.toJson());
            
                            return responseBody.toString();
                        } else {
                            //Respond to client
                            response.setContentType("application/json");
                            response.setStatus(500);
            
                            JSONObject responseBody = new JSONObject();
                            responseBody.put("message", "Failed to create folder structure on disk. Contact the developers ASAP");
                            return responseBody.toString();
                        }
                    } else {
                        //Respond to client
                        response.setContentType("application/json");
                        response.setStatus(500);
        
                        JSONObject responseBody = new JSONObject();
                        responseBody.put("message", "Failed to create CNAME record. Contact the developers ASAP.");
                        return responseBody.toString();
                    }
                } else {
                    GroupBlog blog = new GroupBlog();
                    blog.setBlogId(UUID.randomUUID());
                    blog.setBlogId(UUID.randomUUID());
                    blog.setBaseUrl(url);
                    blog.setCompleteUrl("https://" + url + ".startapped.com");
                    blog.getOwners().add(account.getAccountId());
                    blog.setName(name);
                    blog.setDescription(description);
                    blog.setNsfw(false);
                    blog.setAllowUnder18(true);
                    
                    //set default images and colors
                    blog.setIconUrl(GlobalVars.cdnUrl + "/img/default/profile.jpg");
                    blog.setBackgroundUrl(GlobalVars.cdnUrl + "/img/default/background.jpg");
                    blog.setBackgroundColor("#ffffff");

                    BlogDataHandler.get().createOrUpdateBlog(blog);
    
                    //Lets get that CNAME record created...
                    if (CloudFlareIntegrator.get().createCNAMEForBlog(blog)) {
        
                        //Create the folders... that's super important
                        if (FileHandler.createDefaultBlogFolders(blog)) {
                            //Respond to client
                            response.setContentType("application/json");
                            response.setStatus(200);
            
                            JSONObject responseBody = new JSONObject();
                            responseBody.put("message", "Success");
                            responseBody.put("blog", blog.toJson());
            
                            return responseBody.toString();
                        } else {
                            //Respond to client
                            response.setContentType("application/json");
                            response.setStatus(500);
            
                            JSONObject responseBody = new JSONObject();
                            responseBody.put("message", "Failed to create folder structure on disk. Contact the developers ASAP");
                            return responseBody.toString();
                        }
                    } else {
                        //Respond to client
                        response.setContentType("application/json");
                        response.setStatus(500);
        
                        JSONObject responseBody = new JSONObject();
                        responseBody.put("message", "Failed to create CNAME record. Contact the developers ASAP.");
                        return responseBody.toString();
                    }
                }
            }
        } catch (JSONException | IllegalArgumentException e) {
            e.printStackTrace();
            
            response.setContentType("application/json");
            response.setStatus(400);
            return ResponseUtils.getJsonResponseMessage("Bad Request");
        } catch (Exception e) {
            Logger.getLogger().exception("Failed to handle blog creation", e, BlogEndpoint.class);
            
            response.setContentType("application/json");
            response.setStatus(500);
            return ResponseUtils.getJsonResponseMessage("Internal Server Error");
        }
    }
    
    @SuppressWarnings("RedundantCast")
    @PostMapping(value = "/get", produces = "application/json")
    public static String get(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody) {
        //Authenticate...
        AuthenticationState authState = Authentication.authenticate(request);
        if (!authState.isSuccess()) {
            response.setStatus(authState.getStatus());
            response.setContentType("application/json");
            return authState.toJson();
        }
        
        try {
            JSONObject body = new JSONObject(requestBody);
            if (body.has("url")) {
                //Get by URL
                String url = Sanitizer.sanitizeBlogUrl(body.getString("url"));

                IBlog blog = BlogDataHandler.get().getBlog(url);
                
                if (blog != null) {
                    response.setContentType("application/json");
                    response.setStatus(200);
                    
                    JSONObject responseBody = new JSONObject();
                    responseBody.put("message", "Success");
                    if (blog instanceof GroupBlog) {
                        responseBody.put("blog", ((GroupBlog) blog).toJson());
                    } else {
                        responseBody.put("blog", ((PersonalBlog) blog).toJson());
                    }
                    
                    return responseBody.toString();
                } else {
                    response.setContentType("application/json");
                    response.setStatus(404);
                    return ResponseUtils.getJsonResponseMessage("Blog not Found");
                }
            } else if (body.has("id")) {
                //Get by blog ID
                UUID id = UUID.fromString(body.getString("id"));
                IBlog blog = BlogDataHandler.get().getBlog(id);
    
                if (blog != null) {
                    response.setContentType("application/json");
                    response.setStatus(200);
        
                    JSONObject responseBody = new JSONObject();
                    responseBody.put("message", "Success");
                    if (blog instanceof GroupBlog) {
                        responseBody.put("blog", ((GroupBlog) blog).toJson());
                    } else {
                        responseBody.put("blog", ((PersonalBlog) blog).toJson());
                    }
        
                    return responseBody.toString();
                } else {
                    response.setContentType("application/json");
                    response.setStatus(404);
                    return ResponseUtils.getJsonResponseMessage("Blog not Found");
                }
            } else if (body.has("all")) {
                List<IBlog> blogs = BlogDataHandler.get().getBlogs(authState.getId());
    
                JSONArray jBlogs = new JSONArray();
                for (IBlog b : blogs) {
                    if (b.getType() == BlogType.PERSONAL)
                        jBlogs.put(((PersonalBlog) b).toJson());
                    else
                        jBlogs.put(((GroupBlog) b).toJson());
                }
    
                //Respond to client
                response.setContentType("application/json");
                response.setStatus(200);
    
                JSONObject responseBody = new JSONObject();
                responseBody.put("message", "Success");
                responseBody.put("blogs", jBlogs);
                responseBody.put("count", jBlogs.length());
    
                return responseBody.toString();
            } else {
                response.setContentType("application/json");
                response.setStatus(400);
                return ResponseUtils.getJsonResponseMessage("Bad Request");
            }
        } catch (JSONException | IllegalArgumentException e) {
            e.printStackTrace();
            
            response.setContentType("application/json");
            response.setStatus(400);
            return ResponseUtils.getJsonResponseMessage("Bad Request");
        } catch (Exception e) {
            Logger.getLogger().exception("Failed to handle blog get", e, BlogEndpoint.class);
            
            response.setContentType("application/json");
            response.setStatus(500);
            return ResponseUtils.getJsonResponseMessage("Internal Server Error");
        }
    }
    
    @PostMapping(value = "/update", produces = "application/json")
    public static String update(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody) {
        //Authenticate...
        AuthenticationState authState = Authentication.authenticate(request);
        if (!authState.isSuccess()) {
            response.setStatus(authState.getStatus());
            response.setContentType("application/json");
            return authState.toJson();
        }
        
        //Okay, now handle actual request.
        Account account = AccountDataHandler.get().getAccountFromId(authState.getId());
        
        try {
            JSONObject body = new JSONObject(requestBody);
            UUID blogId = UUID.fromString(body.getString("id"));

            IBlog blogRaw = BlogDataHandler.get().getBlog(blogId);
            if (blogRaw != null) {
                if (blogRaw.getType() == BlogType.PERSONAL) {
                    PersonalBlog blog = (PersonalBlog) blogRaw;
                    if (blog.getOwnerId().equals(account.getAccountId()) || account.isAdmin()) {
                        //TODO: Support changing URL
                        if (body.has("name"))
                            blog.setName(Sanitizer.sanitizeUserInput(body.getString("name")));
                        if (body.has("description"))
                            blog.setDescription(Sanitizer.sanitizeUserInput(body.getString("description")));
                        if (body.has("display_age"))
                            blog.setDisplayAge(body.getBoolean("display_age"));
                        if (body.has("nsfw"))
                            blog.setNsfw(body.getBoolean("nsfw"));
                        if (body.has("allow_under_18"))
                            blog.setAllowUnder18(body.getBoolean("allow_under_18"));
                        if (body.has("background_color")) {
                            //Validate color -- Fail silently and let the rest of the edit go through.
                            if (Validator.validColorCode(body.getString("background_color")))
                                blog.setBackgroundColor(body.getString("background_color"));
                        }
                        if (body.has("icon_image")) {
                            UploadedFile file = FileUploadHandler.handleBase64Upload(body.getJSONObject("icon_image"), request, account.getAccountId(), MimeType.IMAGE);
                            if (file != null)
                                blog.setIconUrl(file.getUrl());
                        }
                        if (body.has("background_image")) {
                            UploadedFile file = FileUploadHandler.handleBase64Upload(body.getJSONObject("background_image"), request, account.getAccountId(), MimeType.IMAGE);
                            if (file != null) {
                                blog.setBackgroundUrl(file.getUrl());
                            }
                        }

                        BlogDataHandler.get().createOrUpdateBlog(blog);
                        
                        //Respond to client
                        response.setContentType("application/json");
                        response.setStatus(200);
                        
                        JSONObject responseBody = new JSONObject();
                        responseBody.put("message", "Success");
                        responseBody.put("blog", blog.toJson());
                        
                        return responseBody.toString();
                    } else {
                        //Does not have permission to edit blog...
                        response.setContentType("application/json");
                        response.setStatus(403);
                        return ResponseUtils.getJsonResponseMessage("Forbidden");
                    }
                } else {
                    GroupBlog blog = (GroupBlog) blogRaw;
                    if (blog.getOwners().contains(account.getAccountId()) || account.isAdmin()) {
                        //TODO: Support changing URL
                        if (body.has("name"))
                            blog.setName(Sanitizer.sanitizeUserInput(body.getString("name")));
                        if (body.has("description"))
                            blog.setDescription(Sanitizer.sanitizeUserInput(body.getString("description")));
                        if (body.has("nsfw"))
                            blog.setNsfw(body.getBoolean("nsfw"));
                        if (body.has("allow_under_18"))
                            blog.setAllowUnder18(body.getBoolean("allow_under_18"));
                        if (body.has("background_color")) {
                            //Validate color -- Fail silently and let the rest of the edit go through.
                            if (Validator.validColorCode(body.getString("background_color")))
                                blog.setBackgroundColor(body.getString("background_color"));
                        }
                        if (body.has("icon_image")) {
                            UploadedFile file = FileUploadHandler.handleBase64Upload(body.getJSONObject("icon_image"), request, account.getAccountId(), MimeType.IMAGE);
                            if (file != null)
                                blog.setIconUrl(file.getUrl());
                        }
                        if (body.has("background_image")) {
                            UploadedFile file = FileUploadHandler.handleBase64Upload(body.getJSONObject("background_image"), request, account.getAccountId(), MimeType.IMAGE);
                            if (file != null) {
                                blog.setBackgroundUrl(file.getUrl());
                            }
                        }

                        BlogDataHandler.get().createOrUpdateBlog(blog);
                        
                        //Respond to client
                        response.setContentType("application/json");
                        response.setStatus(200);
                        
                        JSONObject responseBody = new JSONObject();
                        responseBody.put("message", "Success");
                        responseBody.put("blog", blog.toJson());
                        
                        return responseBody.toString();
                    } else {
                        //Does not have permission to edit blog...
                        response.setContentType("application/json");
                        response.setStatus(403);
                        return ResponseUtils.getJsonResponseMessage("Forbidden");
                    }
                }
            } else {
                response.setContentType("application/json");
                response.setStatus(404);
                return ResponseUtils.getJsonResponseMessage("Blog not Found");
            }
        } catch (JSONException | IllegalArgumentException e) {
            e.printStackTrace();
            
            response.setContentType("application/json");
            response.setStatus(400);
            return ResponseUtils.getJsonResponseMessage("Bad Request");
        } catch (Exception e) {
            Logger.getLogger().exception("Failed to handle blog update", e, BlogEndpoint.class);
            
            response.setContentType("application/json");
            response.setStatus(500);
            return ResponseUtils.getJsonResponseMessage("Internal Server Error");
        }
    }
}
