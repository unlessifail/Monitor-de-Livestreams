package com.youtube.livemonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class YouTubeLiveMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(YouTubeLiveMonitorApplication.class, args);
    }
}
