package com.mbld.jigslybackend.entities.dto;

import lombok.Builder;

@Builder
public record PuzzlePieceDto(Integer idX, Integer idY, Float[] position, Integer group) {
}
