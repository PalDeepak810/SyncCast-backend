package com.Syncast.user.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(

            HttpServletRequest request,

            HttpServletResponse response,

            FilterChain filterChain

    ) throws ServletException, IOException {

        System.out.println("JWT FILTER HIT");

        System.out.println(
                request.getRequestURI()
        );

        System.out.println(
                request.getHeader("Authorization")
        );

        final String authHeader =
                request.getHeader("Authorization");

        final String jwtToken;

        final String userEmail;


        if (
                authHeader == null ||
                        !authHeader.startsWith("Bearer ")
        ) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        try {
            jwtToken =
                    authHeader.substring(7);


            userEmail =
                    jwtService.extractUsername(
                            jwtToken
                    );

            System.out.println(
                    "Extracted User: " + userEmail
            );
            if (
                    userEmail != null &&
                            SecurityContextHolder
                                    .getContext()
                                    .getAuthentication() == null
            ) {

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(
                                        userEmail
                                );


                if (
                        jwtService.isTokenValid(
                                jwtToken,
                                userDetails
                        )
                ) {

                    UsernamePasswordAuthenticationToken
                            authToken =
                            new UsernamePasswordAuthenticationToken(

                                    userDetails,

                                    null,

                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(

                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );


                    SecurityContextHolder
                            .getContext()
                                    .setAuthentication(authToken);
                }

                System.out.println(
                        "AUTHENTICATED SUCCESSFULLY"
                );
            }
        } catch (Exception e) {
            System.err.println("JWT Authentication failed: " + e.getMessage());
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}