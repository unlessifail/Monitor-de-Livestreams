package com.youtube.livemonitor.controller;

import com.youtube.livemonitor.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@Slf4j
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<String> verifySubscription(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.topic") String topic,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam(value = "hub.lease_seconds", required = false) String leaseSeconds) {
        
        log.info("Verificação de subscrição recebida - Mode: {}, Topic: {}", mode, topic);
        
        return ResponseEntity.ok(challenge);
    }
    
    @PostMapping(consumes = {"application/xml", "application/atom+xml"})
    public ResponseEntity<Void> receiveNotification(@RequestBody String xmlBody) {
        log.info("Notificação recebida do YouTube Hub");
        log.debug("XML Body: {}", xmlBody);
        
        notificationService.processNotification(xmlBody);
        
        return ResponseEntity.ok().build();
    }
}
