package com.qkl.edu_system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ppt")
public class PptConfig {
    private String appId;
    private String secret;
    private String baseUrl;
}
