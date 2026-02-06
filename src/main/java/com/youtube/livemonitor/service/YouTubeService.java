package com.youtube.livemonitor.service;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.youtube.livemonitor.dto.ChannelSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class YouTubeService {
    
    @Autowired
    private YouTube youTube;
    
    @Value("${youtube.api.key}")
    private String apiKey;
    
    public List<ChannelSearchResult> searchChannels(String query) throws IOException {
        List<ChannelSearchResult> results = new ArrayList<>();
        String handleOrId = extractHandleOrIdFromUrl(query);
        
        if (handleOrId != null) {
            log.info("Buscando canal por handle/ID: {}", handleOrId);
            Channel channel = getChannelByHandleOrId(handleOrId);
            if (channel != null) {
                results.add(convertToSearchResult(channel));
                return results;
            }
        }
        
        log.info("Buscando canais por query: {}", query);
        YouTube.Search.List search = youTube.search().list(List.of("snippet"));
        search.setKey(apiKey);
        search.setQ(query);
        search.setType(List.of("channel"));
        search.setMaxResults(5L);
        
        SearchListResponse searchResponse = search.execute();
        
        for (SearchResult result : searchResponse.getItems()) {
            String channelId = result.getSnippet().getChannelId();
            Channel channel = getChannelById(channelId);
            if (channel != null) {
                results.add(convertToSearchResult(channel));
            }
        }
        
        return results;
    }
    
    private String extractHandleOrIdFromUrl(String url) {
        Pattern handlePattern = Pattern.compile("youtube\\.com/@([^/?&#]+)");
        Matcher handleMatcher = handlePattern.matcher(url);
        if (handleMatcher.find()) {
            return "@" + handleMatcher.group(1);
        }
        
        Pattern channelPattern = Pattern.compile("youtube\\.com/channel/([^/?&#]+)");
        Matcher channelMatcher = channelPattern.matcher(url);
        if (channelMatcher.find()) {
            return channelMatcher.group(1);
        }
        
        return null;
    }
    
    private Channel getChannelByHandleOrId(String handleOrId) throws IOException {
        YouTube.Channels.List request = youTube.channels()
            .list(List.of("snippet", "statistics"));
        request.setKey(apiKey);
        
        if (handleOrId.startsWith("@")) {
        	request.setForUsername(handleOrId.replace("@", ""));
        } else {
            request.setId(List.of(handleOrId));
        }
        
        ChannelListResponse response = request.execute();
        
        if (response.getItems() != null && !response.getItems().isEmpty()) {
            return response.getItems().get(0);
        }
        
        return null;
    }
    
    private Channel getChannelById(String channelId) throws IOException {
        YouTube.Channels.List request = youTube.channels()
            .list(List.of("snippet", "statistics"));
        request.setKey(apiKey);
        request.setId(List.of(channelId));
        
        ChannelListResponse response = request.execute();
        
        if (response.getItems() != null && !response.getItems().isEmpty()) {
            return response.getItems().get(0);
        }
        
        return null;
    }
    
    private ChannelSearchResult convertToSearchResult(Channel channel) {
        ChannelSearchResult result = new ChannelSearchResult();
        result.setChannelId(channel.getId());
        result.setDisplayName(channel.getSnippet().getTitle());
        result.setUrl("https://youtube.com/channel/" + channel.getId());
        result.setDescription(channel.getSnippet().getDescription());
        
        if (channel.getSnippet().getThumbnails() != null && 
            channel.getSnippet().getThumbnails().getDefault() != null) {
            result.setThumbnailUrl(channel.getSnippet().getThumbnails().getDefault().getUrl());
        }
        
        if (channel.getStatistics() != null && channel.getStatistics().getSubscriberCount() != null) {
            result.setSubscriberCount(channel.getStatistics().getSubscriberCount().longValue());
        }
        
        return result;
    }
    
    public boolean isVideoLive(String videoId) {
        try {
            YouTube.Videos.List request = youTube.videos()
                .list(List.of("snippet", "liveStreamingDetails"));
            request.setKey(apiKey);
            request.setId(List.of(videoId));
            
            VideoListResponse response = request.execute();
            
            if (response.getItems() != null && !response.getItems().isEmpty()) {
                Video video = response.getItems().get(0);
                
                if (video.getLiveStreamingDetails() != null) {
                    String broadcastStatus = video.getSnippet().getLiveBroadcastContent();
                    return "live".equals(broadcastStatus);
                }
            }
        } catch (IOException e) {
            log.error("Erro ao verificar status de live do vídeo {}: {}", videoId, e.getMessage());
        }
        
        return false;
    }
    
    public String getChannelName(String channelId) {
        try {
            Channel channel = getChannelById(channelId);
            if (channel != null) {
                return channel.getSnippet().getTitle();
            }
        } catch (IOException e) {
            log.error("Erro ao obter nome do canal {}: {}", channelId, e.getMessage());
        }
        return channelId;
    }
}
