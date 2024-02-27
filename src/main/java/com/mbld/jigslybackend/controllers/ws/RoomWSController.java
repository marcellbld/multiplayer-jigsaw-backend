package com.mbld.jigslybackend.controllers.ws;

import com.mbld.jigslybackend.entities.Role;
import com.mbld.jigslybackend.entities.dto.PublicUserDto;
import com.mbld.jigslybackend.entities.dto.PuzzlePieceDto;
import com.mbld.jigslybackend.entities.dto.RoomUserDto;
import com.mbld.jigslybackend.entities.wsmessages.*;
import com.mbld.jigslybackend.entities.wsmessages.room.RoomInitialDataDto;
import com.mbld.jigslybackend.entities.wsmessages.room.RoomPuzzleMoveDto;
import com.mbld.jigslybackend.entities.wsmessages.room.RoomPuzzleReleaseDto;
import com.mbld.jigslybackend.services.PuzzleService;
import com.mbld.jigslybackend.services.RoomService;
import com.mbld.jigslybackend.services.ws.RoomWSService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class RoomWSController {

    private final RoomService roomService;
    private final PuzzleService puzzleService;
    private final RoomWSService roomWSService;

    @SubscribeMapping("/room/{id}")
    public SocketMessage<RoomInitialDataDto> joinToRoom(
            @DestinationVariable String id,
            SimpMessageHeaderAccessor accessor) throws Exception {

        AbstractAuthenticationToken authenticationToken = getAbstractAuthenticationToken(accessor);
        RoomUserDto roomUserDto = roomService.addUserToRoom(id, convertAuthenticationTokenToPublicUserDto(authenticationToken));

        roomWSService.notifyRoom_RoomUserJoined(id, roomUserDto);
        return roomWSService.create_RoomInitialData(roomService.getRoomDto(id));
    }

    private static AbstractAuthenticationToken getAbstractAuthenticationToken(SimpMessageHeaderAccessor accessor) throws Exception {
        AbstractAuthenticationToken authenticationToken = (AbstractAuthenticationToken) accessor.getUser();

        if(authenticationToken == null) throw new Exception("Not authenticated user. Connection close.");
        return authenticationToken;
    }

    @MessageMapping("/room/{id}/puzzle/move")
    public void movePiece(@DestinationVariable("id") String roomId,
                          @RequestBody RoomPuzzleMoveDto roomPuzzleMoveDto,
                          SimpMessageHeaderAccessor accessor) throws Exception {

        AbstractAuthenticationToken authenticationToken = getAbstractAuthenticationToken(accessor);
        PuzzlePieceDto piece = roomPuzzleMoveDto.puzzlePiece();

        puzzleService.movePiece(roomId, piece);
        roomWSService.notifyRoom_PuzzleMove(roomId, authenticationToken.getName(), piece);
    }

    @MessageMapping("/room/{id}/puzzle/release")
    public void releasePiece(@DestinationVariable("id") String roomId,
                             @RequestBody RoomPuzzleReleaseDto roomPuzzleReleaseDto,
                             SimpMessageHeaderAccessor accessor) throws Exception {

        AbstractAuthenticationToken authenticationToken = getAbstractAuthenticationToken(accessor);
        PuzzlePieceDto pieceDto = roomPuzzleReleaseDto.puzzlePiece();

        PuzzlePieceDto[] changedPieceDtos = puzzleService.releasePiece(roomId, pieceDto);
        pieceDto = puzzleService.getPuzzlePiece(roomId, pieceDto.idX(), pieceDto.idY());

        roomWSService.notifyRoom_PuzzleRelease(roomId, pieceDto, changedPieceDtos);
    }

    private PublicUserDto convertAuthenticationTokenToPublicUserDto(AbstractAuthenticationToken authenticationToken) {
        return PublicUserDto.builder()
                .username(authenticationToken.getName())
                .role(Role.valueOf(authenticationToken.getAuthorities().stream().findFirst().orElseThrow().getAuthority()))
                .build();
    }
}
