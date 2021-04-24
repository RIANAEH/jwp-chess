package chess.domain.piece;

import java.util.Map;

import chess.domain.board.Score;
import chess.domain.position.Direction;
import chess.domain.position.Position;

public class Rook extends Piece {

    private static final String NAME = "r";
    private static final Score SCORE = new Score(5);

    public Rook(Color color) {
        super(NAME, color, SCORE);
    }

    @Override
    public boolean canMove(Map<Position, Piece> board, Position source, Position target) {
        if (source.isNotLinearPosition(target)) {
            return false;
        }
        Direction direction = Direction.linearTargetDirection(source.difference(target));
        do {
            source = source.sum(direction);
        } while (!source.equals(target)
            && board.get(source).isEmpty() && source.isChessBoardPosition());
        return source.equals(target);
    }

}
