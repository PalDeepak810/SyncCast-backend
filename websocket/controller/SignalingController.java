package com.Syncast.websocket.controller;

import com.Syncast.websocket.dto.SignalingMessage;
import com.Syncast.websocket.service.RoomPresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class SignalingController {

    private final SimpMessagingTemplate messagingTemplate;

    private final RoomPresenceService roomPresenceService;

    @MessageMapping("/signal")
    public void handleSignalingMessage(
            @Payload SignalingMessage message
    ) {

        String roomCode = message.getRoomCode();

        String sender = message.getSender();

        if ("JOIN".equals(message.getType())) {

            roomPresenceService.joinRoom(
                    roomCode,
                    sender
            );
        }

        if ("LEAVE".equals(message.getType())) {

            roomPresenceService.leaveRoom(
                    roomCode,
                    sender
            );
        }


        messagingTemplate.convertAndSend(
                "/topic/room/" + roomCode,
                message
        );

        Set<String> participants =
                roomPresenceService.getParticipants(roomCode);

        messagingTemplate.convertAndSend(
                "/topic/room/" + roomCode + "/participants",
                participants
        );
    }
}