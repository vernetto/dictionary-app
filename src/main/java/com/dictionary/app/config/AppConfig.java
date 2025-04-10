package com.dictionary.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;
import java.io.File;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Value("${dictionary.files.path}")
    private String dictionaryFilesPath;
    
    @PostConstruct
    public void init() {
        // Create dictionary files directory if it doesn't exist
        File directory = new File(dictionaryFilesPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
}
