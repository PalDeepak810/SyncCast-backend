package com.Syncast.websocket.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomPresenceService {

    /*
        roomCode -> users
     */
    private final Map<String, Set<String>> roomParticipants =
            new ConcurrentHashMap<>();

    public void joinRoom(
            String roomCode,
            String username
    ) {

        roomParticipants.computeIfAbsent(
                roomCode,
                key -> ConcurrentHashMap.newKeySet()
        );

        roomParticipants.get(roomCode)
                .add(username);
    }

    public void leaveRoom(
            String roomCode,
            String username
    ) {

        Set<String> participants =
                roomParticipants.get(roomCode);

        if (participants != null) {

            participants.remove(username);

            /*
                Cleanup empty rooms
             */
            if (participants.isEmpty()) {

                roomParticipants.remove(roomCode);
            }
        }
    }

    public Set<String> getParticipants(
            String roomCode
    ) {

        return roomParticipants.getOrDefault(
                roomCode,
                Collections.emptySet()
        );
    }

    public boolean isUserInRoom(
            String roomCode,
            String username
    ) {

        return roomParticipants
                .getOrDefault(
                        roomCode,
                        Collections.emptySet()
                )
                .contains(username);
    }
}