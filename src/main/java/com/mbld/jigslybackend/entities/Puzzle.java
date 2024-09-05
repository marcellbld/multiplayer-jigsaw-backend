package com.mbld.jigslybackend.entities;

import com.mbld.jigslybackend.constants.PuzzleConstants;
import com.mbld.jigslybackend.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.util.*;

@RedisHash("puzzles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Puzzle {

    @Id
    private String id;
    private PuzzleImage puzzleImage;
    private Integer realPieces, pieces;
    private Integer worldWidth, worldHeight;
    private Integer pieceWidth, pieceHeight;
    private PuzzlePiece[] puzzlePieces;
    public Puzzle(String id, int pieces, PuzzleImage puzzleImage) {
        this.id = id;
        this.puzzleImage = puzzleImage;
        this.worldWidth = (int)(puzzleImage.getWidth() * PuzzleConstants.WORLD_WIDTH_OFFSET_MULTIPLIER);
        this.worldHeight = (int)(puzzleImage.getHeight() * PuzzleConstants.WORLD_HEIGHT_OFFSET_MULTIPLIER);

        this.realPieces = pieces;
        int piecesX = getPiecesX();
        int piecesY = getPiecesY();

        this.pieces = piecesX * piecesY;
        this.pieceWidth = puzzleImage.getWidth()/piecesX;
        this.pieceHeight = puzzleImage.getHeight()/piecesY;

        this.puzzlePieces = initPieces(piecesX, piecesY);
    }

    public int getPiecesX() {
        double sqrtPieces = Math.sqrt(realPieces);
        return (int)(sqrtPieces*((double)puzzleImage.getWidth()/ puzzleImage.getHeight()));
    }
    public int getPiecesY() {
        double sqrtPieces = Math.sqrt(realPieces);
        return (int)(sqrtPieces*((double)puzzleImage.getHeight()/ puzzleImage.getWidth()));
    }

    private PuzzlePiece[] initPieces(int piecesX, int piecesY) {
        PuzzlePiece[] pieces = new PuzzlePiece[piecesX*piecesY];

        float puzzleWidth = puzzleImage.getWidth();
        float puzzleHeight = puzzleImage.getHeight();
        float lowerX = (float)worldWidth/2f- puzzleWidth +pieceWidth/2f;
        float upperX = (float)worldWidth/2f-puzzleWidth/2f-pieceWidth*2f;
        float lowerY = (float)worldHeight/2f-puzzleHeight/2f-puzzleHeight/6f-pieceHeight/2f;
        float upperY = (float)worldHeight/2f+puzzleHeight/2f+puzzleHeight/6f-pieceHeight/2f;

        for(int i = 0; i < piecesY; i++) {
            for(int j = 0; j < piecesX; j++) {
                int id = calculatePieceId(j, i);

                PuzzlePiece piece = new PuzzlePiece(j,i,id, calculateRealX(j), calculateRealY(i));
                float randX = ((float)Math.random() * (upperX - lowerX)) + lowerX;
                float randY = ((float)Math.random() * (upperY - lowerY)) + lowerY;

                piece.setPosition(randX+(Math.random() < 0.5 ? worldWidth/2f : 0), randY);

                pieces[id] = piece;
            }
        }
        return pieces;
    }

    public void movePiece(int idX, int idY, float x, float y) {
        PuzzlePiece piece = getPiece(idX, idY);
        piece.setPosition(x,y);

        Set<PuzzlePiece> movedPieces = new HashSet<>();
        Queue<PuzzlePiece> queue = new LinkedList<>();
        queue.add(piece);

        while(!queue.isEmpty()) {
            PuzzlePiece currentPiece = queue.poll();

            movePiece_processAdjacentPieces(currentPiece, PuzzlePieceSideType.TOP, movedPieces, queue);
            movePiece_processAdjacentPieces(currentPiece, PuzzlePieceSideType.RIGHT, movedPieces, queue);
            movePiece_processAdjacentPieces(currentPiece, PuzzlePieceSideType.BOTTOM, movedPieces, queue);
            movePiece_processAdjacentPieces(currentPiece, PuzzlePieceSideType.LEFT, movedPieces, queue);

            movedPieces.add(currentPiece);
        }
    }

    private void movePiece_processAdjacentPieces(PuzzlePiece currentPiece, PuzzlePieceSideType sideType,
                                       Set<PuzzlePiece> movedPieces, Queue<PuzzlePiece> queue) {
        PuzzlePiece adjacentPiece = getAdjacentPiece(currentPiece, sideType);
        if (adjacentPiece != null && !movedPieces.contains(adjacentPiece) && (int) adjacentPiece.getGroup() == currentPiece.getGroup()) {
            adjacentPiece.setPositionByPiece(currentPiece);
            queue.add(adjacentPiece);
        }
    }
    public PuzzlePiece[] releasePiece(int idX, int idY, float x, float y) {
        List<PuzzlePiece> changedPieces = new ArrayList<>();

        PuzzlePiece piece = getPiece(idX, idY);
        piece.setPosition(x,y);

        Set<PuzzlePiece> checkedPieces = new HashSet<>();
        Queue<PuzzlePiece> queue = new LinkedList<>();
        queue.add(piece);

        final int group = piece.getGroup();
        while(!queue.isEmpty()) {
            PuzzlePiece currentPiece = queue.poll();

            updatePiecePositionAndGroup(changedPieces, piece, currentPiece);

            releasePiece_processAdjacentPieces(currentPiece, PuzzlePieceSideType.TOP, changedPieces, checkedPieces, queue, group);
            releasePiece_processAdjacentPieces(currentPiece, PuzzlePieceSideType.RIGHT, changedPieces, checkedPieces, queue, group);
            releasePiece_processAdjacentPieces(currentPiece, PuzzlePieceSideType.BOTTOM, changedPieces, checkedPieces, queue, group);
            releasePiece_processAdjacentPieces(currentPiece, PuzzlePieceSideType.LEFT, changedPieces, checkedPieces, queue, group);

            checkedPieces.add(currentPiece);
        }

        return changedPieces.toArray(new PuzzlePiece[0]);
    }

    private void releasePiece_processAdjacentPieces(PuzzlePiece currentPiece, PuzzlePieceSideType sideType,
                                       List<PuzzlePiece> changedPieces, Set<PuzzlePiece> checkedPieces, Queue<PuzzlePiece> queue,
                                       int group) {
        PuzzlePiece adjacentPiece = getAdjacentPiece(currentPiece, sideType);
        if (adjacentPiece != null) {
            if (checkPieceMatch(currentPiece, adjacentPiece, sideType) && adjacentPiece.getGroup() != group && adjacentPiece.getGroup() != -9999) {
                adjacentPiece.setGroup(currentPiece.getGroup());
                changedPieces.add(adjacentPiece);
            }

            if (adjacentPiece.getGroup() == group && !checkedPieces.contains(adjacentPiece)) {
                queue.add(adjacentPiece);
            }
        }
    }

    private PuzzlePiece getAdjacentPiece(PuzzlePiece currentPiece, PuzzlePieceSideType sideType) {
        int offsetX = 0, offsetY = 0;
        switch (sideType) {
            case TOP -> offsetY = -1;
            case RIGHT -> offsetX = 1;
            case BOTTOM -> offsetY = 1;
            case LEFT -> offsetX = -1;
        }
        return getPiece(currentPiece.getIdX() + offsetX, currentPiece.getIdY() + offsetY);
    }
    private void updatePiecePositionAndGroup(List<PuzzlePiece> changedPieces, PuzzlePiece piece, PuzzlePiece currentPiece) {
        float realX = currentPiece.getRealX(), realY = currentPiece.getRealY();
        float dist = MathUtils.calculateDistance(realX, realY, currentPiece.getX(), currentPiece.getY());

        if(piece.getGroup() == -9999 || (currentPiece.getGroup() != -9999 && dist <= pieceHeight * 0.25f)){
            currentPiece.setGroup(-9999);
            currentPiece.setPosition(realX, realY);
            if(currentPiece != piece) {
                changedPieces.add(currentPiece);
            }
        } else if(currentPiece.getGroup() == -9999) {
            currentPiece.setPosition(realX, realY);
        } else {
            currentPiece.setPositionByPiece(piece);
        }
    }

    private boolean checkPieceMatch(PuzzlePiece piece, PuzzlePiece piece2, PuzzlePieceSideType sideType) {
        return piece2 != null && piecesMatch(piece, piece2, sideType);
    }

    public PuzzlePiece getPiece(int idX, int idY) {
        if(idY < 0 || idX < 0 || idX >= getPiecesX() || idY >= getPiecesY()) {
            return null;
        }

        return this.puzzlePieces[calculatePieceId(idX, idY)];
    }

    private int calculatePieceId(int idX, int idY) {
        return idY* getPiecesX() + idX;
    }
    private boolean piecesMatch(PuzzlePiece piece, PuzzlePiece piece2, PuzzlePieceSideType sideType) {
        float relativeX = piece2.getX();
        float relativeY = piece2.getY();

        switch (sideType) {
            case TOP -> relativeY += pieceHeight;
            case RIGHT -> relativeX -= pieceWidth;
            case BOTTOM -> relativeY -= pieceHeight;
            case LEFT -> relativeX += pieceWidth;
        }

        float dist = MathUtils.calculateDistance(relativeX, relativeY, piece.getX(),  piece.getY());
        return dist <= pieceHeight * 0.25f;
    }

    private float calculateRealX(int x) {
        return worldWidth/2f-puzzleImage.getWidth()/2f-pieceWidth/4f+x*pieceWidth;
    }
    private float calculateRealY(int y) {
        return worldHeight/2f-puzzleImage.getHeight()/2f-pieceHeight/4f+y*pieceHeight;
    }
}
