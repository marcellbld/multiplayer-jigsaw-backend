package com.mbld.jigslybackend.entities.wsmessages.room;

import com.mbld.jigslybackend.entities.dto.PublicUserDto;
import com.mbld.jigslybackend.entities.dto.RoomUserDto;
import lombok.Builder;

@Builder
public record RoomUserJoinedDto(RoomUserDto user) {
}
