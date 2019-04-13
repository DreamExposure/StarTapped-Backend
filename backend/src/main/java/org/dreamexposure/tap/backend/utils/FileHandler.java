package org.dreamexposure.tap.backend.utils;

import org.apache.commons.io.FileUtils;
import org.dreamexposure.tap.core.conf.SiteSettings;
import org.dreamexposure.tap.core.objects.blog.IBlog;
import org.dreamexposure.tap.core.utils.Logger;

import java.io.File;

/**
 * @author NovaFox161
 * Date Created: 12/12/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class FileHandler {
    public static boolean createDefaultBlogFolders(IBlog blog) {

        boolean success = false;

        try {
            boolean s = (new File(SiteSettings.BLOG_FOLDER.get() + "/" + blog.getBaseUrl() + "/public")).mkdirs();

            if (s) {
                File source = new File(SiteSettings.TEMPLATE_FOLDER.get() + "/blog/index.html");
                File dest = new File(SiteSettings.BLOG_FOLDER.get() + "/" + blog.getBaseUrl() + "/public/index.html");

                FileUtils.copyFile(source, dest);

                success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger().exception("Failed to create blog directory and file", e, true, FileHandler.class);
            success = false;
        }

        return success;
    }
}
