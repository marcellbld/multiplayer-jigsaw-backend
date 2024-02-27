package com.mbld.jigslybackend.entities.wsmessages.room;

import com.mbld.jigslybackend.entities.dto.PuzzlePieceDto;
import lombok.Builder;

@Builder
public record RoomPuzzleReleaseDto(PuzzlePieceDto puzzlePiece, PuzzlePieceDto[] changedPieces) {
}
