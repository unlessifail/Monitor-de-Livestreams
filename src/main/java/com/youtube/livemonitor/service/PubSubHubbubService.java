package com.youtube.livemonitor.service;

import com.youtube.livemonitor.entity.Channel;
import com.youtube.livemonitor.repository.ChannelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class PubSubHubbubService {
    
    @Autowired
    private ChannelRepository channelRepository;
    
    @Value("${youtube.hub.url}")
    private String hubUrl;
    
    @Value("${youtube.feed.url}")
    private String feedUrl;
    
    @Value("${app.callback.url}")
    private String callbackUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public boolean subscribeToChannel(String channelId) {
        try {
            String topicUrl = feedUrl + "?channel_id=" + channelId;
            
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("hub.mode", "subscribe");
            params.add("hub.topic", topicUrl);
            params.add("hub.callback", callbackUrl);
            params.add("hub.verify", "async");
            params.add("hub.lease_seconds", "432000");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            log.info("Subscrevendo canal {} no Hub", channelId);
            ResponseEntity<String> response = restTemplate.postForEntity(hubUrl, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Subscrição iniciada com sucesso para o canal {}", channelId);
                
                Channel channel = channelRepository.findById(channelId).orElse(null);
                if (channel != null) {
                    channel.setSubscribed(true);
                    channel.setLastSubscriptionDate(LocalDateTime.now());
                    channelRepository.save(channel);
                }
                
                return true;
            }
            
            log.error("Falha na subscrição. Status: {}", response.getStatusCode());
            return false;
            
        } catch (Exception e) {
            log.error("Erro ao subscrever canal {}: {}", channelId, e.getMessage(), e);
            return false;
        }
    }
    
    public boolean unsubscribeFromChannel(String channelId) {
        try {
            String topicUrl = feedUrl + "?channel_id=" + channelId;
            
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("hub.mode", "unsubscribe");
            params.add("hub.topic", topicUrl);
            params.add("hub.callback", callbackUrl);
            params.add("hub.verify", "async");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            log.info("Desinscrevendo canal {} do Hub", channelId);
            ResponseEntity<String> response = restTemplate.postForEntity(hubUrl, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Desinscrição iniciada com sucesso para o canal {}", channelId);
                
                Channel channel = channelRepository.findById(channelId).orElse(null);
                if (channel != null) {
                    channel.setSubscribed(false);
                    channelRepository.save(channel);
                }
                
                return true;
            }
            
            log.error("Falha na desinscrição. Status: {}", response.getStatusCode());
            return false;
            
        } catch (Exception e) {
            log.error("Erro ao desinscrever canal {}: {}", channelId, e.getMessage(), e);
            return false;
        }
    }
    
    @Scheduled(fixedRate = 172800000)
    public void renewSubscriptions() {
        log.info("Iniciando renovação de subscrições...");
        
        List<Channel> subscribedChannels = channelRepository.findBySubscribedTrue();
        
        for (Channel channel : subscribedChannels) {
            log.info("Renovando subscrição para o canal: {}", channel.getDisplayName());
            subscribeToChannel(channel.getId());
        }
        
        log.info("Renovação de subscrições concluída. Total: {} canais", subscribedChannels.size());
    }
}
