package com.mbld.jigslybackend.entities.wsmessages.lobby;

import com.mbld.jigslybackend.entities.dto.RoomDto;
import lombok.Builder;

@Builder
public record LobbyRoomCreatedDto(RoomDto room) {
}
