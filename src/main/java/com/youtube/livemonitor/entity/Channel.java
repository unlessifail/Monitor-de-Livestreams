package com.youtube.livemonitor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "channels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
    @Id
    private String id; // Este é o ID do canal (ex: UC...)
    
    @Column(nullable = false)
    private String displayName;
    
    @Column(nullable = false)
    private String url; // Link do canal no YouTube

    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl; // NOVO: Campo para o ícone que estava faltando!
    
    @Column
    private LocalDateTime lastSubscriptionDate;
    
    @Column
    private boolean subscribed = false;
}