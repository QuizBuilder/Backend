package com.quizBuilder.project.Config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${ai.service.url}")
    private String aiServiceURL;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(aiServiceURL)
                .build();
    }
}

