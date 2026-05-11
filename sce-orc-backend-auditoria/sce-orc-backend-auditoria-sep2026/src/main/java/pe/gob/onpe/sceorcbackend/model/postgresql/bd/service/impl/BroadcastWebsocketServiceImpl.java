package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class BroadcastWebsocketServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(BroadcastWebsocketServiceImpl.class);

    private final SimpMessagingTemplate messagingTemplate;

    public BroadcastWebsocketServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastProgressUpdate(String destination, int progress) {
        try {
            messagingTemplate.convertAndSend(destination, progress);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }
    }

    public void broadcastProgressUpdate(String destination, String jsonPayload) {
        try {
            messagingTemplate.convertAndSend(destination, jsonPayload);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }
    }

}
