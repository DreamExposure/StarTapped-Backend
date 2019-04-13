package org.dreamexposure.tap.backend.conf;

import org.dreamexposure.tap.core.conf.SiteSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
public class LettuceConfig {
    @SuppressWarnings("deprecation")
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        final LettuceConnectionFactory factory = new LettuceConnectionFactory();

        factory.setHostName(SiteSettings.REDIS_HOST.get());
        factory.setPort(Integer.valueOf(SiteSettings.REDIS_PORT.get()));
        factory.setPassword(SiteSettings.REDIS_PASS.get());

        return factory;
    }
}
