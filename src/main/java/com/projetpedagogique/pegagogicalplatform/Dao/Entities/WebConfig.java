package com.projetpedagogique.pegagogicalplatform.Dao.Entities;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Assurez-vous que le chemin est correct
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/Users/youne/Pictures/PegagogicalPlatform/PegagogicalPlatform/src/main/resources/uploads/");
    }
}
