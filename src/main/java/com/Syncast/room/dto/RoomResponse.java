package com.Syncast.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {
    private Long id;
    private String roomCode;
    private String status;
    private String hostUsername;
    private LocalDateTime createdAt;

}
