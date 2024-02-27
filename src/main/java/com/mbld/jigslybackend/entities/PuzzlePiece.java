package com.mbld.jigslybackend.entities;

import lombok.Data;
import java.io.Serializable;

@Data
public class PuzzlePiece implements Serializable {

    private final Integer idX;
    private final Integer idY;
    private Float realX, realY;
    private Float x, y;
    private Integer group;
    public PuzzlePiece(int idX, int idY, int group, float realX, float realY) {
        this.idX = idX;
        this.idY = idY;
        this.group = group;
        this.realX = realX;
        this.realY = realY;
    }
    public void setPositionByPiece(PuzzlePiece piece) {
        float newX = piece.getX() + getRealX() - piece.getRealX();
        float newY = piece.getY() + getRealY() - piece.getRealY();

        setPosition(newX, newY);
    }
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
