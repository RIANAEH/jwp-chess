package chess.domain.piece;

import java.util.Map;

import chess.domain.board.Score;
import chess.domain.position.Position;
import chess.exception.IllegalMoveException;

public abstract class Piece {

    private final String name;
    private final Color color;
    private final Score score;

    protected Piece(String name, Color color, Score score) {
        this.name = name;
        this.color = color;
        this.score = score;
    }

    public Piece(String name) {
        this.name = name;
        this.color = Color.NONE;
        this.score = null;
    }

    public boolean isSameColor(Piece piece) {
        return piece.isNotEmpty() && this.color == piece.color;
    }

    public String getName() {
        if (isNotEmpty() && isBlack()) {
            return name.toUpperCase();
        }
        return name;
    }

    public void validateMove(Map<Position, Piece> board, Position source, Position target) {
        if (!canMove(board, source, target)) {
            throw new IllegalMoveException();
        }
    }

    public Score getScore() {
        return score;
    }

    public boolean isBlack() {
        return color.isBlack();
    }

    public Color getColor() {
        return color;
    }

    public boolean isNotEmpty() {
        return true;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean isKing() {
        return false;
    }

    public boolean isPawn() {
        return false;
    }

    public boolean isAlly(Color color) {
        return this.color.equals(color);
    }

    protected abstract boolean canMove(Map<Position, Piece> board, Position source,
        Position target);

}
