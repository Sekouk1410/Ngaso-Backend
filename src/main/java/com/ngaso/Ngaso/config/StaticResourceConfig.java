package com.ngaso.Ngaso.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Resolve absolute path to the local uploads directory
        Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
        String uploadLocation = uploadDir.toUri().toString();
        if (!uploadLocation.endsWith("/")) {
            uploadLocation = uploadLocation + "/";
        }

        // Serve files placed under the local 'uploads/' directory at URL path '/uploads/**'
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation)
                .setCachePeriod(3600) // 1 hour caching
                .resourceChain(true);
    }
}
