package org.dreamexposure.tap.frontend;

import org.dreamexposure.tap.core.utils.Logger;
import org.dreamexposure.tap.frontend.conf.FrontendSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author NovaFox161
 * Date Created: 12/6/2018
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SpringBootApplication
public class TapFrontend {
    
    public static void main(String[] args) throws IOException {
        //Load settings
        Properties p = new Properties();
        p.load(new FileReader(new File("settings.properties")));
        FrontendSettings.init(p);
        
        Logger.getLogger().init(FrontendSettings.LOG_FOLDER.get());
        
        
        //Init Spring
        SpringApplication.run(TapFrontend.class, args);
    }
}
