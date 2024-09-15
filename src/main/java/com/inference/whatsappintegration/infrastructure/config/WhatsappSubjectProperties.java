package com.inference.whatsappintegration.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "property.whatsapp.subjects")
public class WhatsappSubjectProperties {

    private Map<String, String> cascade = new HashMap<>();

    public Map<String, String> getCascade() {
        return cascade;
    }

    public void setCascade(Map<String, String> cascade) {
        this.cascade = cascade;
    }
}
