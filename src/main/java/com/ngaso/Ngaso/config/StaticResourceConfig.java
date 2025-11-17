package com.ngaso.Ngaso.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${app.upload.root}")
    private String uploadRoot;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expose the physical upload directory under the /uploads/** URL path
        // Example: if app.upload.root=C:/ngaso-uploads, then
        //   /uploads/illustrations/1/file.png -> C:/ngaso-uploads/illustrations/1/file.png
        String location = "file:" + (uploadRoot.endsWith("/") || uploadRoot.endsWith("\\")
                ? uploadRoot
                : uploadRoot + "/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
