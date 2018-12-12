package org.dreamexposure.tap.backend.api.v1.endpoints;

import org.dreamexposure.tap.backend.network.auth.Authentication;
import org.dreamexposure.tap.backend.network.database.DatabaseHandler;
import org.dreamexposure.tap.backend.objects.auth.AuthenticationState;
import org.dreamexposure.tap.backend.utils.ResponseUtils;
import org.dreamexposure.tap.core.enums.blog.BlogType;
import org.dreamexposure.tap.core.objects.account.Account;
import org.dreamexposure.tap.core.objects.blog.GroupBlog;
import org.dreamexposure.tap.core.objects.blog.IBlog;
import org.dreamexposure.tap.core.objects.blog.PersonalBlog;
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
        
        //Okay, now handle actual request.
        Account account = DatabaseHandler.getHandler().getAccountFromId(authState.getId());
        
        try {
            JSONObject body = new JSONObject(requestBody);
            //Lets get all those variables
            String url = body.getString("url");
            BlogType type = BlogType.valueOf(body.getString("type"));
            String name = account.getUsername() + "'s blog";
            String description = "Lorem Ipsum";
            
            //Check if URL is taken...
            if (DatabaseHandler.getHandler().blogUrlTaken(url)) {
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
                    blog.setIconUrl("https://cdn.startapped.com/img/default/profile.jpg");
                    blog.setBackgroundUrl("https://cdn.startapped.com/img/default/background.jpg");
                    blog.setBackgroundColor("#ffffff");
                    
                    DatabaseHandler.getHandler().createOrUpdateBlog(blog);
                    
                    //Respond to client
                    response.setContentType("application/json");
                    response.setStatus(200);
                    
                    JSONObject responseBody = new JSONObject();
                    responseBody.put("message", "Success");
                    responseBody.put("blog", blog.toJson());
                    
                    return responseBody.toString();
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
                    blog.setIconUrl("https://cdn.startapped.com/img/default/profile.jpg");
                    blog.setBackgroundUrl("https://cdn.startapped.com/img/default/background.jpg");
                    blog.setBackgroundColor("#ffffff");
                    
                    DatabaseHandler.getHandler().createOrUpdateBlog(blog);
                    
                    //Respond to client
                    response.setContentType("application/json");
                    response.setStatus(200);
                    
                    JSONObject responseBody = new JSONObject();
                    responseBody.put("message", "Success");
                    responseBody.put("blog", blog.toJson());
                    
                    return responseBody.toString();
                }
            }
        } catch (JSONException e) {
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
                String url = body.getString("url");
                
                IBlog blog = DatabaseHandler.getHandler().getBlog(url);
                
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
                IBlog blog = DatabaseHandler.getHandler().getBlog(id);
                
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
            } else {
                response.setContentType("application/json");
                response.setStatus(400);
                return ResponseUtils.getJsonResponseMessage("Bad Request");
            }
        } catch (JSONException e) {
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
        Account account = DatabaseHandler.getHandler().getAccountFromId(authState.getId());
        
        try {
            JSONObject body = new JSONObject(requestBody);
            UUID blogId = UUID.fromString(body.getString("id"));
            
            IBlog blogRaw = DatabaseHandler.getHandler().getBlog(blogId);
            if (blogRaw != null) {
                if (blogRaw.getType() == BlogType.PERSONAL) {
                    PersonalBlog blog = (PersonalBlog) blogRaw;
                    if (blog.getOwnerId().equals(account.getAccountId()) || account.isAdmin()) {
                        //TODO: Support changing URL
                        if (body.has("name"))
                            blog.setName(body.getString("name"));
                        if (body.has("description"))
                            blog.setDescription(body.getString("description"));
                        if (body.has("display_age"))
                            blog.setDisplayAge(body.getBoolean("display_age"));
                        if (body.has("nsfw"))
                            blog.setNsfw(body.getBoolean("nsfw"));
                        if (body.has("allow_under_18"))
                            blog.setAllowUnder18(body.getBoolean("allow_under_18"));
                        if (body.has("background_color"))
                            blog.setBackgroundColor(body.getString("background_color"));
                        //TODO: Support changing icon URL and background URL.
                        
                        DatabaseHandler.getHandler().createOrUpdateBlog(blog);
                        
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
                            blog.setName(body.getString("name"));
                        if (body.has("description"))
                            blog.setDescription(body.getString("description"));
                        if (body.has("nsfw"))
                            blog.setNsfw(body.getBoolean("nsfw"));
                        if (body.has("allow_under_18"))
                            blog.setAllowUnder18(body.getBoolean("allow_under_18"));
                        if (body.has("background_color"))
                            blog.setBackgroundColor(body.getString("background_color"));
                        //TODO: Support changing icon URL and background URL.
                        
                        DatabaseHandler.getHandler().createOrUpdateBlog(blog);
                        
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
        } catch (JSONException e) {
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
}