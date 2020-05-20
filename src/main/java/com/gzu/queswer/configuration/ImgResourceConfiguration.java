package com.gzu.queswer.configuration;

import com.gzu.queswer.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImgResourceConfiguration implements WebMvcConfigurer {

    @Value("${resourceLocation}")
    String resourceLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        FileUtil.setFilePath(resourceLocation);
        registry.addResourceHandler("/img/**").addResourceLocations("file:" + resourceLocation);
    }
}
