package com.Syncast.user.service;

import com.Syncast.user.dto.AuthResponse;
import com.Syncast.user.dto.LoginRequest;
import com.Syncast.user.dto.RegisterRequest;
import com.Syncast.user.entity.User;
import com.Syncast.user.repository.UserRepo;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setUsername(request.getUsername());

        user.setEmail(request.getEmail());

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        User savedUser = userRepository.save(user);

        org.springframework.security.core.userdetails.UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .builder()
                        .username(savedUser.getEmail())
                        .password(savedUser.getPasswordHash())
                        .roles("USER")
                        .build();

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                token
        );
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        org.springframework.security.core.userdetails.UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .builder()
                        .username(user.getEmail())
                        .password(user.getPasswordHash())
                        .roles("USER")
                        .build();

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                token
        );
    }
}