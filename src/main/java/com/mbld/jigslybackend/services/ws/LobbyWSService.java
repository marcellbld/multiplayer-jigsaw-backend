package com.mbld.jigslybackend.services.ws;

import com.mbld.jigslybackend.entities.dto.RoomDto;
import com.mbld.jigslybackend.entities.wsmessages.*;
import com.mbld.jigslybackend.entities.wsmessages.lobby.LobbyInitialDataDto;
import com.mbld.jigslybackend.entities.wsmessages.lobby.LobbyRoomCreatedDto;
import com.mbld.jigslybackend.entities.wsmessages.lobby.LobbyRoomRemovedDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LobbyWSService {
    private final SimpMessagingTemplate messagingTemplate;
    private static final String LOBBY_TOPIC = "/topic/lobby";

    public LobbyWSService(@Lazy SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    public void notifyLobby_LobbyRoomCreated(RoomDto room) {
        LobbyRoomCreatedDto messageBody = LobbyRoomCreatedDto.builder()
                .room(room)
                .build();

        messagingTemplate.convertAndSend(LOBBY_TOPIC,
                SocketMessage.builder()
                        .event(SocketEventType.Lobby_RoomCreated)
                        .body(messageBody)
                        .build());
    }
    public void notifyLobby_LobbyRoomRemoved(RoomDto room) {
        LobbyRoomRemovedDto messageBody = LobbyRoomRemovedDto.builder()
                .room(room)
                .build();

        messagingTemplate.convertAndSend(LOBBY_TOPIC,
                SocketMessage.builder()
                        .event(SocketEventType.Lobby_RoomRemoved)
                        .body(messageBody)
                        .build());
    }

    public SocketMessage<LobbyInitialDataDto> create_LobbyInitialData(List<RoomDto> rooms) {
        LobbyInitialDataDto messageBody = LobbyInitialDataDto.builder()
                .rooms(rooms)
                .build();

        return SocketMessage.<LobbyInitialDataDto>builder()
                .event(SocketEventType.Lobby_InitialData)
                .body(messageBody)
                .build();
    }

}
