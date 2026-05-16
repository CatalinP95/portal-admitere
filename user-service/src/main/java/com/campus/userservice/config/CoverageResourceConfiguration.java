package com.campus.userservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class CoverageResourceConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String userDir = System.getProperty("user.dir");
        Path direct = Paths.get(userDir, "target", "site", "jacoco");
        Path withSubmodule = Paths.get(userDir, "user-service", "target", "site", "jacoco");
        Path jacocoDir = Files.exists(direct) ? direct : withSubmodule;
        String location = "file:" + jacocoDir.toString().replace("\\", "/") + "/";
        registry.addResourceHandler("/coverage/**")
                .addResourceLocations(location);
    }
}
