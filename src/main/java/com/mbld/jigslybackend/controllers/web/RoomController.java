package com.mbld.jigslybackend.controllers.web;

import com.mbld.jigslybackend.annotations.ValidImage;
import com.mbld.jigslybackend.entities.dto.RoomDto;
import com.mbld.jigslybackend.entities.wsmessages.lobby.LobbyCreateRoomDto;
import com.mbld.jigslybackend.services.RoomService;
import com.mbld.jigslybackend.services.ws.LobbyWSService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final LobbyWSService lobbyWSService;
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<RoomDto> createRoom(@RequestPart(value="file") @ValidImage MultipartFile file,
                                              @RequestPart(value="dto") LobbyCreateRoomDto createRoomDto) throws Exception {

        String prefix = "data:%s;base64,".formatted(file.getContentType());
        RoomDto room = roomService.createRoom(createRoomDto, prefix + Base64.getEncoder().encodeToString(file.getBytes()));

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
