package com.youtube.livemonitor.config;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YouTubeConfig {
    
    @Value("${youtube.api.key}")
    private String apiKey;
    
    @Bean
    public YouTube youTube() {
        return new YouTube.Builder(
            new NetHttpTransport(),
            new GsonFactory(),
            request -> {}
        )
        .setApplicationName("YouTube-Live-Monitor")
        .build();
    }
}
