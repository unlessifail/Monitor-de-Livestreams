package com.youtube.livemonitor.service;

import com.youtube.livemonitor.dto.ChannelSearchResult;
import com.youtube.livemonitor.entity.Channel;
import com.youtube.livemonitor.repository.ChannelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ChannelService {
    
    @Autowired
    private ChannelRepository channelRepository;
    
    @Autowired
    private YouTubeService youTubeService;
    
    @Autowired
    private PubSubHubbubService pubSubHubbubService;
    
    public List<ChannelSearchResult> searchChannels(String query) throws Exception {
        log.info("Buscando canais com query: {}", query);
        return youTubeService.searchChannels(query);
    }
    
    @Transactional
    public Channel confirmAndAddChannel(String channelId) throws Exception {
        log.info("Confirmando adição do canal: {}", channelId);
        
        if (channelRepository.existsById(channelId)) {
            log.warn("Canal {} já está sendo monitorado", channelId);
            return channelRepository.findById(channelId).orElseThrow();
        }
        
        List<ChannelSearchResult> results = youTubeService.searchChannels(channelId);
        if (results.isEmpty()) {
            throw new Exception("Canal não encontrado: " + channelId);
        }
        
        ChannelSearchResult channelDetails = results.get(0);
        
        Channel channel = new Channel();
        channel.setId(channelDetails.getChannelId());
        channel.setDisplayName(channelDetails.getDisplayName());
        channel.setUrl(channelDetails.getUrl());
        // ATUALIZAÇÃO: Agora salvamos a thumbnail no banco de dados
        channel.setThumbnailUrl(channelDetails.getThumbnailUrl()); 
        channel.setSubscribed(false);
        
        channel = channelRepository.save(channel);
        log.info("Canal salvo no banco com imagem: {} - {}", channel.getDisplayName(), channel.getId());
        
        boolean subscribed = pubSubHubbubService.subscribeToChannel(channelId);
        
        if (subscribed) {
            channel.setSubscribed(true);
            channel.setLastSubscriptionDate(LocalDateTime.now());
            channel = channelRepository.save(channel);
            log.info("Inscrição ativa no hub para canal: {}", channel.getDisplayName());
        } else {
            log.error("Falha ao inscrever no hub para canal: {}", channel.getDisplayName());
        }
        
        return channel;
    }
    
    public List<Channel> listAllChannels() {
        return channelRepository.findAll();
    }
    
    public List<Channel> listActiveChannels() {
        return channelRepository.findBySubscribedTrue();
    }
    
    @Transactional
    public void removeChannel(String channelId) {
        log.info("Removendo canal: {}", channelId);
        
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Canal não encontrado: " + channelId));
        
        if (channel.isSubscribed()) {
            pubSubHubbubService.unsubscribeFromChannel(channelId);
        }
        
        channelRepository.delete(channel);
        log.info("Canal removido: {} - {}", channel.getDisplayName(), channelId);
    }
    
    public Channel getChannel(String channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Canal não encontrado: " + channelId));
    }
}