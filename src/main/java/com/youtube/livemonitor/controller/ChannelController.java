package com.youtube.livemonitor.controller;

import com.youtube.livemonitor.dto.ApiResponse;
import com.youtube.livemonitor.dto.ChannelSearchResult;
import com.youtube.livemonitor.entity.Channel;
import com.youtube.livemonitor.service.ChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/channels")
@Slf4j
@CrossOrigin(origins = "*") // [SOLUÇÃO] Permite que o Front-end acesse os dados sem erro de CORS
public class ChannelController {
    
    @Autowired
    private ChannelService channelService;
    
    @PostMapping("/search")
    public ResponseEntity<ApiResponse> searchChannels(@RequestParam String query) {
        log.info("Recebida requisição de busca: {}", query); // Log para conferir com seu terminal
        try {
            List<ChannelSearchResult> results = channelService.searchChannels(query);
            
            if (results.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("Nenhum canal encontrado para a query: " + query));
            }
            
            // Retorna a lista de canais para o app.js renderizar os cards
            return ResponseEntity.ok(ApiResponse.success(
                "Busca concluída com sucesso.",
                results
            ));
            
        } catch (Exception e) {
            log.error("Erro ao buscar canais: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Erro ao buscar canais: " + e.getMessage()));
        }
    }
    
    @PostMapping("/confirm/{channelId}")
    public ResponseEntity<ApiResponse> confirmChannel(@PathVariable String channelId) {
        log.info("Iniciando confirmação do canal ID: {}", channelId);
        try {
            Channel channel = channelService.confirmAndAddChannel(channelId);
            return ResponseEntity.ok(ApiResponse.success(
                "Canal adicionado com sucesso ao monitoramento!",
                channel
            ));
        } catch (Exception e) {
            log.error("Erro ao confirmar canal: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Erro ao adicionar canal: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse> listAllChannels() {
        List<Channel> channels = channelService.listAllChannels();
        return ResponseEntity.ok(ApiResponse.success(
            "Total de canais monitorados: " + channels.size(),
            channels
        ));
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse> listActiveChannels() {
        List<Channel> channels = channelService.listActiveChannels();
        return ResponseEntity.ok(ApiResponse.success(
            "Total de canais com subscrição ativa: " + channels.size(),
            channels
        ));
    }
    
    @DeleteMapping("/{channelId}")
    public ResponseEntity<ApiResponse> removeChannel(@PathVariable String channelId) {
        try {
            channelService.removeChannel(channelId);
            return ResponseEntity.ok(ApiResponse.success("Canal removido com sucesso!"));
        } catch (Exception e) {
            log.error("Erro ao remover canal: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Erro ao remover canal: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{channelId}")
    public ResponseEntity<ApiResponse> getChannel(@PathVariable String channelId) {
        try {
            Channel channel = channelService.getChannel(channelId);
            return ResponseEntity.ok(ApiResponse.success("Canal encontrado", channel));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}