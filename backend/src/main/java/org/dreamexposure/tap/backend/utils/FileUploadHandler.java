package org.dreamexposure.tap.backend.utils;

import org.dreamexposure.novautils.crypto.KeyGenerator;
import org.dreamexposure.tap.backend.conf.GlobalVars;
import org.dreamexposure.tap.backend.conf.SiteSettings;
import org.dreamexposure.tap.core.enums.file.MimeType;
import org.dreamexposure.tap.core.objects.file.UploadedFile;
import org.dreamexposure.tap.core.utils.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/15/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileUploadHandler {
    private static char[] VALID_CHARACTERS_2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456879_-".toCharArray(); //File hash chars
    
    public static void init() {
        new File(SiteSettings.TMP_FOLDER.get()).mkdirs();
        
        for (MimeType mt : MimeType.values()) {
            new File(SiteSettings.UPLOAD_FOLDER.get() + "/" + mt.getFolder()).mkdirs();
        }
    }
    
    public static UploadedFile handleBase64Upload(JSONObject json, HttpServletRequest request, UUID uploader, MimeType allowedType) throws JSONException {
        String hash = KeyGenerator.csRandomString(11, VALID_CHARACTERS_2);
        
        String base64 = json.getString("encoded");
        String unconfirmedContentType = json.getString("type");
        
        String contentType = unconfirmedContentType;
        
        //Convert to file and validate content type
        byte[] bytes = Base64.getDecoder().decode(base64);
        
        try {
            //Save file to tmp
            OutputStream outputStream = new FileOutputStream(SiteSettings.TMP_FOLDER.get() + "/" + hash + ".tmp");
    
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
            
            File tmpFile = new File(SiteSettings.TMP_FOLDER.get() + "/" + hash + ".tmp");
    
            //Validate file size (CloudFlare limits max upload to 100Mb, so we don't have to worry about it exceeding that on write.
            long fileSizeInMb = tmpFile.length() / (1024 * 1024);
            if (fileSizeInMb > 10) {
                //File too big... delete
                tmpFile.delete();
        
                return null;
            }
            
            //Validate content type...
            InputStream is = new BufferedInputStream(new FileInputStream(tmpFile));
            String mimeType = URLConnection.guessContentTypeFromStream(is);
            is.close();
            if (!mimeType.equals(unconfirmedContentType))
                contentType = mimeType;
            
            //Is this content type allowed?
            if (contentType.equalsIgnoreCase(allowedType.getFormalName()) || (contentType.contains("/") && contentType.split("/")[0].equalsIgnoreCase(allowedType.getFormalName()))) {
                
                //Scan for wiruses
                List<String> viruses = AntiVirus.scan(tmpFile);
                
                if (viruses.size() > 0) {
                    //Viruses found...
                    tmpFile.delete();
                    
                    Logger.getLogger().api("[VIRUS] " + viruses.toString(), request.getRemoteAddr(), request.getRemoteHost(), request.getRequestURL().toString());
                    return null;
                }
                //Save file to CDN
                Files.move(tmpFile.toPath(), new File(SiteSettings.UPLOAD_FOLDER.get() + "/" + allowedType.getFolder() + "/" + hash).toPath(), StandardCopyOption.ATOMIC_MOVE);
                
                File uploaded = new File(SiteSettings.UPLOAD_FOLDER.get() + "/" + allowedType.getFolder() + "/" + hash);
                
                //Save details to database
                UploadedFile uploadedFile = new UploadedFile();
                uploadedFile.setHash(hash);
                uploadedFile.setPath(uploaded.getPath());
                uploadedFile.setType(mimeType);
                uploadedFile.setUrl(GlobalVars.cdnUrl + "/" + allowedType.getFolder() + "/" + hash); //Ex cdn.startapped.com/image/blah
                uploadedFile.setUploader(uploader);
                
                //TODO: Add to database (is this really needed?)
                
                return uploadedFile;
            } else {
                //File type isn't allowed...
                tmpFile.delete();
                
                return null;
            }
        } catch (IOException e) {
            //Failed
            return null;
        }
    }
}
