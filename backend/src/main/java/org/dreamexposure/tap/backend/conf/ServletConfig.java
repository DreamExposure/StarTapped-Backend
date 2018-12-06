package org.dreamexposure.tap.backend.conf;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * @author NovaFox161
 * Date Created: 12/4/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@Configuration
@EnableAutoConfiguration
public class ServletConfig {
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return (container -> {
            container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/"));
            container.setPort(Integer.valueOf(SiteSettings.PORT.get()));
        });
    }
}