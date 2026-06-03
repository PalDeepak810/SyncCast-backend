package com.Syncast.room.controller;

import com.Syncast.room.dto.CreateRoomRequest;
import com.Syncast.room.dto.JoinRoomRequest;
import com.Syncast.room.dto.RoomResponse;
import com.Syncast.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom() {

        System.out.println("Starting new meeting / creating room...");

        RoomResponse response =
                roomService.createRoom();

        System.out.println("Room created: " + response.getRoomCode());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/join")
    public ResponseEntity<RoomResponse> joinRoom(
            @RequestBody JoinRoomRequest request
    ) {

        System.out.println("User joining room: " + request.getRoomCode());

        RoomResponse response =
                roomService.joinRoom(request);

        System.out.println("User joined successfully: " + request.getRoomCode());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomCode}")
    public ResponseEntity<RoomResponse> getRoom(
            @PathVariable String roomCode
    ) {

        RoomResponse response =
                roomService.getRoomByCode(roomCode);

        return ResponseEntity.ok(response);
    }
}