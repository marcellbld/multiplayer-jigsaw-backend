package com.mbld.jigslybackend.entities.wsmessages.lobby;

import com.mbld.jigslybackend.entities.dto.RoomDto;
import lombok.Builder;

import java.util.List;

@Builder
public record LobbyInitialDataDto(List<RoomDto> rooms) {
}
