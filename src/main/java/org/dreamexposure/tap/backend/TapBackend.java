package org.dreamexposure.tap.backend;

import org.dreamexposure.tap.backend.conf.SiteSettings;
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
        
        //Logger.getLogger().init();
        
        //Init database
        //DatabaseManager.getManager().connectToMySQL();
        //DatabaseManager.getManager().createTables();
        
        //Init Spring
        //AccountHandler.getHandler().init();
        SpringApplication.run(TapBackend.class, args);
        
        //Init the rest of our services
        //EmailHandler.getHandler().init();
    }
}
