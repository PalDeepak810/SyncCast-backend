package com.Syncast.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,length = 50)
    private String username;
    @Column(nullable = false,length = 100)
    private String email;
    @Column(name = "password_hash",nullable = false,length = 255)
    private String passwordHash;
    @Column(name = "created_at")
    private LocalDateTime created_at;
}
