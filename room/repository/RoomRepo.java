package com.Syncast.room.repository;


import com.Syncast.room.entity.Room;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepo
        extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomCode(String roomCode);

    boolean existsByRoomCode(String roomCode);
}