package com.mbld.jigslybackend.controllers.ws;

import com.mbld.jigslybackend.entities.wsmessages.*;
import com.mbld.jigslybackend.entities.wsmessages.lobby.LobbyInitialDataDto;
import com.mbld.jigslybackend.services.RoomService;
import com.mbld.jigslybackend.services.ws.LobbyWSService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class LobbyWSController {

    private final RoomService roomService;
    private final LobbyWSService lobbyWSService;

    @SubscribeMapping("/lobby")
    public SocketMessage<LobbyInitialDataDto> joinToLobby() {
        return lobbyWSService.create_LobbyInitialData(roomService.getRooms());
    }
}
