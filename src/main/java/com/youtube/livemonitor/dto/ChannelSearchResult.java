package com.youtube.livemonitor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelSearchResult {
    private String channelId;
    private String displayName;
    private String url;
    private String thumbnailUrl;
    private String description;
    private Long subscriberCount;
}
