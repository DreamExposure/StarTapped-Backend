package org.dreamexposure.tap.backend.utils;

import org.dreamexposure.tap.core.conf.SiteSettings;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author NovaFox161
 * Date Created: 12/15/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class AntiVirus {
    private static ClamavClient client;
    
    public static void init() {
        client = new ClamavClient(SiteSettings.CLAM_HOST.get(), Integer.parseInt(SiteSettings.CLAM_PORT.get()));
    }
    
    
    public static List<String> scan(File file) {
        //CLAM NO SCAN IS ONLY ON IN DEV ENVIRONMENT!!
        if (!Boolean.valueOf(SiteSettings.CLAM_NO_SCAN.get())) {
            ScanResult result = client.scan(file.toPath());
            if (result instanceof ScanResult.OK) {
                // OK - Return empty list.
                return new ArrayList<>();
            } else if (result instanceof ScanResult.VirusFound) {
                Map<String, Collection<String>> viruses = ((ScanResult.VirusFound) result).getFoundViruses();
                return viruses.entrySet().stream().flatMap(a -> a.getValue().stream()).distinct().sorted().collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }
}
