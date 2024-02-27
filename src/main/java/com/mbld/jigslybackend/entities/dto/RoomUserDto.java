package com.mbld.jigslybackend.entities.dto;

import lombok.Builder;

@Builder
public record RoomUserDto(String username, Integer colorId) {

}
