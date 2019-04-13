package org.dreamexposure.tap.backend;

import org.dreamexposure.tap.backend.network.cloudflare.CloudFlareIntegrator;
import org.dreamexposure.tap.backend.network.database.DatabaseHandler;
import org.dreamexposure.tap.backend.network.email.EmailHandler;
import org.dreamexposure.tap.backend.network.google.vision.ImageAnalysis;
import org.dreamexposure.tap.backend.utils.AntiVirus;
import org.dreamexposure.tap.backend.utils.FileUploadHandler;
import org.dreamexposure.tap.core.conf.SiteSettings;
import org.dreamexposure.tap.core.utils.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author NovaFox161
 * Date Created: 12/4/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SpringBootApplication
public class TapBackend {
    
    public static void main(String[] args) throws IOException {
        //Load settings
        Properties p = new Properties();
        p.load(new FileReader(new File("settings.properties")));
        SiteSettings.init(p);
    
        Logger.getLogger().init(SiteSettings.LOG_FOLDER.get());
        
        //Init database
        DatabaseHandler.getHandler().connectToMySQL();
        DatabaseHandler.getHandler().createTables();
        
        //Init Spring
        SpringApplication.run(TapBackend.class, args);
        
        //Init the rest of our services
        EmailHandler.getHandler().init();
        CloudFlareIntegrator.get().init();
        AntiVirus.init();
        FileUploadHandler.init();
        ImageAnalysis.get().init();

        Logger.getLogger().status("All services Enabled!", null);
    }
}
