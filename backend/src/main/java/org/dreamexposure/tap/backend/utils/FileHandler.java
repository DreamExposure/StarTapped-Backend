package org.dreamexposure.tap.backend.utils;

import org.dreamexposure.tap.backend.conf.SiteSettings;
import org.dreamexposure.tap.core.objects.blog.IBlog;

import java.io.File;

/**
 * @author NovaFox161
 * Date Created: 12/12/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings("UnnecessaryLocalVariable")
public class FileHandler {
    public static boolean createDefaultBlogFolders(IBlog blog) {
        
        boolean success = (new File(SiteSettings.BLOG_FOLDER.get() + "/" + blog.getBaseUrl() + "/public")).mkdirs();
        
        //TODO: Create default .html file and everything else that is stupid important!!!!!!!!
        
        return success;
    }
}
