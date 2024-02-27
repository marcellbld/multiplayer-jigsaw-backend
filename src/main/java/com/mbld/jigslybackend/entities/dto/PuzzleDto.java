package com.mbld.jigslybackend.entities.dto;

import lombok.Builder;

@Builder
public record PuzzleDto(
        String imageBase64,
        Integer[] imageSize,
        Integer[] worldSize,
        Integer[] pieceSize,
        Integer[] piecesDimensions,
        PuzzlePieceDto[] puzzlePieces) {
}
