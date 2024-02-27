package com.mbld.jigslybackend.entities.wsmessages.room;

import com.mbld.jigslybackend.entities.dto.RoomDto;
import lombok.Builder;

@Builder
public record RoomInitialDataDto(RoomDto room) {
}
