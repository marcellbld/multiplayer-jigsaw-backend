package com.mbld.jigslybackend.entities.wsmessages.lobby;

import lombok.Builder;

@Builder
public record LobbyCreateRoomDto(Integer pieces, Integer userCapacity, String puzzleImageBase64) {
}
