package com.mbld.jigslybackend.services;

import com.mbld.jigslybackend.entities.*;
import com.mbld.jigslybackend.entities.dto.*;
import com.mbld.jigslybackend.entities.wsmessages.lobby.LobbyCreateRoomDto;
import com.mbld.jigslybackend.repositories.redis.RoomRepository;
import com.mbld.jigslybackend.services.ws.LobbyWSService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final PuzzleService puzzleService;
    private final LobbyWSService lobbyWSService;
    private final RoomRepository roomRepository;

    private String randomRoomId() {
        String randomLetters = RandomStringUtils.randomAlphabetic(3);
        String randomNumbers = RandomStringUtils.randomNumeric(3);

        return randomLetters.toUpperCase() + randomNumbers;
    }
    public void removeRoom(String id) throws Exception {
        RoomDto roomDto = getRoomDto(id);

        roomRepository.deleteById(id);
        puzzleService.removePuzzle(id);

        lobbyWSService.notifyLobby_LobbyRoomRemoved(roomDto);
    }
    public RoomDto createRoom(LobbyCreateRoomDto createRoomDto, String imageBase64) throws Exception {
        String randomRoomId = randomRoomId();
        Puzzle puzzle = puzzleService.createPuzzle(randomRoomId, createRoomDto.pieces(), imageBase64);

        Room room = Room.builder()
                .id(randomRoomId)
                .userCapacity(createRoomDto.userCapacity())
                .users(new CopyOnWriteArrayList<>())
                .puzzle(puzzle)
                .build();
        roomRepository.save(room);

        return convertRoomToDto(room);
    }
    public List<RoomDto> getRooms() {
        return StreamSupport.stream(roomRepository.findAll().spliterator(), false)
                .map(room -> {
                    try {
                        return convertRoomToDto(room);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
    public RoomDto getRoomDto(String id) throws Exception {
        Room room = getRoom(id);

        return convertRoomToDto(room);
    }
    public Room getRoom(String id) throws Exception {
        return roomRepository.findById(id).orElseThrow(() -> new Exception("Room not found by id: " + id));
    }

    public RoomUserDto addUserToRoom(String id, PublicUserDto publicUserDto) throws Exception {
        if(publicUserDto == null) throw new Exception("User cannot be null");

        Room room = getRoom(id);

        RoomUserDto userDto = room.joinUser(publicUserDto);
        roomRepository.save(room);

        return userDto;
    }

    public void removeUserFromRoom(String id, String username) throws Exception {
        if(username == null || username.isBlank()) throw new Exception("User cannot be null");

        Room room = getRoom(id);

        room.detachUser(username);
        roomRepository.save(room);

        if(room.isEmpty()) {
            removeRoom(room.getId());
        }
    }

    private RoomDto convertRoomToDto(Room room) throws Exception {
        return RoomDto.builder()
                .id(room.getId())
                .userCapacity(room.getUserCapacity())
                .users(room.getUsers())
                .puzzle(puzzleService.getPuzzleDto(room.getId()))
                .build();
    }

}
