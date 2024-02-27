package com.mbld.jigslybackend.controllers.web;

import com.mbld.jigslybackend.entities.dto.RoomDto;
import com.mbld.jigslybackend.entities.wsmessages.lobby.LobbyCreateRoomDto;
import com.mbld.jigslybackend.services.RoomService;
import com.mbld.jigslybackend.services.ws.LobbyWSService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final LobbyWSService lobbyWSService;
    @PostMapping()
    public ResponseEntity<RoomDto> createRoom(@RequestBody LobbyCreateRoomDto createRoomDto) throws Exception {
        RoomDto room = roomService.createRoom(createRoomDto);

        lobbyWSService.notifyLobby_LobbyRoomCreated(room);

        return ResponseEntity.ok(room);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoom(@PathVariable String id) throws Exception {
        return ResponseEntity.ok(roomService.getRoomDto(id));
    }

    @GetMapping()
    public ResponseEntity<List<RoomDto>> getRooms() {
        return ResponseEntity.ok(roomService.getRooms());
    }
}
