package com.mbld.jigslybackend.services.ws;

import com.mbld.jigslybackend.entities.dto.PuzzlePieceDto;
import com.mbld.jigslybackend.entities.dto.RoomDto;
import com.mbld.jigslybackend.entities.dto.RoomUserDto;
import com.mbld.jigslybackend.entities.wsmessages.*;
import com.mbld.jigslybackend.entities.wsmessages.room.RoomInitialDataDto;
import com.mbld.jigslybackend.entities.wsmessages.room.RoomPuzzleMoveDto;
import com.mbld.jigslybackend.entities.wsmessages.room.RoomPuzzleReleaseDto;
import com.mbld.jigslybackend.entities.wsmessages.room.RoomUserJoinedDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RoomWSService {

    private final SimpMessagingTemplate messagingTemplate;
    private static final String ROOM_TOPIC = "/topic/room/";

    public RoomWSService(@Lazy SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyRoom_RoomUserJoined(String roomId, RoomUserDto joinedUser) {
        RoomUserJoinedDto messageBody = RoomUserJoinedDto.builder()
                .user(joinedUser)
                .build();

        messagingTemplate.convertAndSend( ROOM_TOPIC + roomId,
                SocketMessage.builder()
                        .event(SocketEventType.Room_UserJoined)
                        .body(messageBody)
                        .build());
    }

    public void notifyRoom_PuzzleMove(String roomId, String username, PuzzlePieceDto puzzlePieceDto) {
        RoomPuzzleMoveDto messageBody = RoomPuzzleMoveDto.builder()
                .username(username)
                .puzzlePiece(puzzlePieceDto)
                .build();

        messagingTemplate.convertAndSend( ROOM_TOPIC + roomId,
                SocketMessage.builder()
                        .event(SocketEventType.Room_Puzzle_Move)
                        .body(messageBody)
                        .build());
    }

    public void notifyRoom_PuzzleRelease(String roomId, PuzzlePieceDto puzzlePieceDto, PuzzlePieceDto[] changedPieceDtos) {
        RoomPuzzleReleaseDto messageBody = RoomPuzzleReleaseDto.builder()
                .puzzlePiece(puzzlePieceDto)
                .changedPieces(changedPieceDtos)
                .build();

        messagingTemplate.convertAndSend( ROOM_TOPIC + roomId,
                SocketMessage.builder()
                        .event(SocketEventType.Room_Puzzle_Release)
                        .body(messageBody)
                        .build());
    }

    public SocketMessage<RoomInitialDataDto> create_RoomInitialData(RoomDto room) {
        RoomInitialDataDto messageBody = RoomInitialDataDto.builder()
                .room(room)
                .build();

        return SocketMessage.<RoomInitialDataDto>builder()
                .event(SocketEventType.Room_InitialData)
                .body(messageBody)
                .build();
    }
}
