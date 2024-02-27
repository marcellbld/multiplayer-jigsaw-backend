package com.mbld.jigslybackend.entities.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record RoomDto(String id, Integer userCapacity, List<RoomUserDto> users, PuzzleDto puzzle) {
}
