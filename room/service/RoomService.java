package com.Syncast.room.service;

import com.Syncast.room.dto.CreateRoomRequest;
import com.Syncast.room.dto.JoinRoomRequest;
import com.Syncast.room.dto.RoomResponse;
import com.Syncast.room.entity.Room;
import com.Syncast.room.repository.RoomRepo;
import com.Syncast.user.entity.User;
import com.Syncast.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepo roomRepository;

    private final UserRepo userRepository;

    public RoomResponse createRoom() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String userEmail =
                authentication.getName();

        User host =
                userRepository.findByEmail(userEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        Room room = new Room();

        room.setHost(host);

        room.setRoomCode(
                generateRoomCode()
        );

    /*
      Default room status
     */
        room.setStatus("ACTIVE");

        room.setCreatedAt(
                LocalDateTime.now()
        );

        Room savedRoom =
                roomRepository.save(room);

        return mapToResponse(savedRoom);
    }
    public RoomResponse joinRoom(JoinRoomRequest request) {

        Room room = roomRepository.findByRoomCode(
                        request.getRoomCode()
                )
                .orElseThrow(() ->
                        new RuntimeException("Room not found")
                );

        return mapToResponse(room);
    }

    public RoomResponse getRoomByCode(String roomCode) {

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() ->
                        new RuntimeException("Room not found")
                );

        return mapToResponse(room);
    }

    private String generateRoomCode() {

        return UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }

    private RoomResponse mapToResponse(Room room) {

        return new RoomResponse(
                room.getId(),
                room.getRoomCode(),
                room.getStatus(),
                room.getHost().getUsername(),
                room.getCreatedAt()
        );
    }
}