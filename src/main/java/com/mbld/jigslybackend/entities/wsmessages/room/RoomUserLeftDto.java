package com.mbld.jigslybackend.entities.wsmessages.room;

import com.mbld.jigslybackend.entities.dto.RoomUserDto;
import lombok.Builder;

@Builder
public record RoomUserLeftDto(RoomUserDto user) {
}
