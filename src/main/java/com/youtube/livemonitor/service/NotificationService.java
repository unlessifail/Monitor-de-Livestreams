package com.youtube.livemonitor.service;

import com.youtube.livemonitor.entity.Channel;
import com.youtube.livemonitor.repository.ChannelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class NotificationService {
    
    @Autowired
    private YouTubeService youTubeService;
    
    @Autowired
    private ChannelRepository channelRepository;
    
    public void processNotification(String xmlBody) {
        try {
            String videoId = extractVideoId(xmlBody);
            String channelId = extractChannelId(xmlBody);
            
            if (videoId == null || channelId == null) {
                log.warn("Não foi possível extrair videoId ou channelId do XML");
                return;
            }
            
            log.info("Notificação recebida - Canal: {}, Vídeo: {}", channelId, videoId);
            
            boolean isLive = youTubeService.isVideoLive(videoId);
            
            if (isLive) {
                Channel channel = channelRepository.findById(channelId).orElse(null);
                String channelName = channel != null ? channel.getDisplayName() : youTubeService.getChannelName(channelId);
                
                String liveUrl = "https://youtube.com/watch?v=" + videoId;
                log.info("[ALERTA DE LIVE] O canal {} está ao vivo agora! Link: {}", channelName, liveUrl);
            } else {
                log.info("O vídeo {} não é uma live ativa.", videoId);
            }
            
        } catch (Exception e) {
            log.error("Erro ao processar notificação: {}", e.getMessage(), e);
        }
    }
    
    private String extractVideoId(String xml) {
        Pattern pattern = Pattern.compile("<yt:videoId>([^<]+)</yt:videoId>");
        Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    private String extractChannelId(String xml) {
        Pattern pattern = Pattern.compile("<yt:channelId>([^<]+)</yt:channelId>");
        Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
