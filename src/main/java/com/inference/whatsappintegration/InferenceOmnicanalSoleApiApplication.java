package com.inference.whatsappintegration;

import com.inference.whatsappintegration.infrastructure.config.WhatsappSubjectProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties(WhatsappSubjectProperties.class)
public class InferenceOmnicanalSoleApiApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
	{
		return application.sources(InferenceOmnicanalSoleApiApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(InferenceOmnicanalSoleApiApplication.class, args);
	}

}
