package com.Syncast.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignalingMessage {

    private String type;

    private String roomCode;

    private String sender;

    private String target;

    private Object data;


}