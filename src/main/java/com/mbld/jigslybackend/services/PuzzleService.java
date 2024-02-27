package com.mbld.jigslybackend.services;

import com.mbld.jigslybackend.entities.Puzzle;
import com.mbld.jigslybackend.entities.PuzzleImage;
import com.mbld.jigslybackend.entities.PuzzlePiece;
import com.mbld.jigslybackend.entities.dto.PuzzleDto;
import com.mbld.jigslybackend.entities.dto.PuzzlePieceDto;
import com.mbld.jigslybackend.repositories.redis.PuzzleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PuzzleService {
    private final PuzzleRepository puzzleRepository;

    public Puzzle createPuzzle(String id, int pieces, String imageBase64) throws IOException {
        Puzzle puzzle = new Puzzle(id, pieces, new PuzzleImage(imageBase64));

        puzzleRepository.save(puzzle);

        return puzzle;
    }

    public Puzzle getPuzzle(String id) throws Exception {
        return puzzleRepository.findById(id).orElseThrow(() -> new Exception("Puzzle not found by id: " + id));
    }

    public void movePiece(String roomId, PuzzlePieceDto puzzlePieceDto) throws Exception {
        Puzzle puzzle = getPuzzle(roomId);

        puzzle.movePiece(
                puzzlePieceDto.idX(),
                puzzlePieceDto.idY(),
                puzzlePieceDto.position()[0],
                puzzlePieceDto.position()[1]
        );
    }

    public PuzzlePieceDto[] releasePiece(String roomId, PuzzlePieceDto puzzlePieceDto) throws Exception {
        Puzzle puzzle = getPuzzle(roomId);

        PuzzlePiece[] changedPieces = puzzle.releasePiece(
                puzzlePieceDto.idX(),
                puzzlePieceDto.idY(),
                puzzlePieceDto.position()[0],
                puzzlePieceDto.position()[1]
        );

        puzzleRepository.save(puzzle);

        return Arrays.stream(changedPieces).map(this::convertBasePuzzlePieceToDto).toList().toArray(new PuzzlePieceDto[0]);
    }

    public void removePuzzle(String id) {
        puzzleRepository.deleteById(id);
    }

    public PuzzlePieceDto getPuzzlePiece(String roomId, int idX, int idY) throws Exception {
        Puzzle puzzle = getPuzzle(roomId);
        return convertPuzzlePieceToDto(puzzle.getPiece(idX, idY));
    }

    public PuzzleDto getPuzzleDto(String id) throws Exception {
        Puzzle puzzle = puzzleRepository.findById(id).orElseThrow(() -> new Exception("Puzzle not found by id: " + id));

        return convertPuzzleToDto(puzzle);
    }

    private PuzzleDto convertPuzzleToDto(Puzzle puzzle) {
        return PuzzleDto.builder()
                .imageBase64(puzzle.getPuzzleImage().getImageBase64())
                .imageSize(new Integer[]{puzzle.getPuzzleImage().getWidth(),puzzle.getPuzzleImage().getHeight()})
                .worldSize(new Integer[]{puzzle.getWorldWidth(), puzzle.getWorldHeight()})
                .pieceSize(new Integer[]{puzzle.getPieceWidth(), puzzle.getPieceHeight()})
                .piecesDimensions(new Integer[]{puzzle.getPiecesX(), puzzle.getPiecesY()})
                .puzzlePieces(Arrays.stream(puzzle.getPuzzlePieces()).map(this::convertPuzzlePieceToDto).toArray(PuzzlePieceDto[]::new))
                .build();
    }

    private PuzzlePieceDto convertBasePuzzlePieceToDto(PuzzlePiece piece){
        return PuzzlePieceDto.builder()
                .idX(piece.getIdX())
                .idY(piece.getIdY())
                .build();
    }
    private PuzzlePieceDto convertPuzzlePieceToDto(PuzzlePiece piece){
        return PuzzlePieceDto.builder()
                .idX(piece.getIdX())
                .idY(piece.getIdY())
                .position(new Float[]{piece.getX(), piece.getY()})
                .group(piece.getGroup())
                .build();
    }
}
