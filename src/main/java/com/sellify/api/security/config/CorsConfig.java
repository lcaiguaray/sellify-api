package com.sellify.api.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    private final CorsProperties corsProperties;

    public CorsConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Bean
    CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowedOrigins(corsProperties.allowedOrigins());
        config.setAllowedHeaders(corsProperties.allowedHeaders());
        config.setAllowedMethods(corsProperties.allowedMethods());
        config.setAllowCredentials(corsProperties.allowCredentials());
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
