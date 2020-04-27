package wooteco.chess.boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.chess.boot.dao.BoardDao;
import wooteco.chess.boot.dto.BoardDto;
import wooteco.chess.boot.dto.GameStatusDto;
import wooteco.chess.domain.board.Board;
import wooteco.chess.domain.board.BoardFactory;
import wooteco.chess.domain.board.Position;
import wooteco.chess.domain.judge.Judge;
import wooteco.chess.domain.piece.Piece;
import wooteco.chess.domain.piece.Team;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChessService {

    @Autowired
    private BoardDao boardDao;

    public Board newGame() throws SQLException {
        Board board = BoardFactory.create();

        writeWholeBoard(board);
        writeCurrentTurn(Team.WHITE);

        return board;
    }

    private void writeWholeBoard(final Board board) throws SQLException {
        BoardDto boardDto = new BoardDto();

        for (Position position : Position.positions) {
            Piece piece = board.findPieceOn(position);

            boardDto.setPosition(position.toString());
            boardDto.setPiece(piece.toString());

            boardDao.placePieceOn(boardDto);
        }
    }

    private void writeCurrentTurn(final Team turn) throws SQLException {
        GameStatusDto gameStatusDto = new GameStatusDto();

        gameStatusDto.setCurrentTeam(turn.toString());
        boardDao.updateTurn(gameStatusDto);
    }

    public Board readBoard() throws SQLException {
        List<BoardDto> boardDtos = boardDao.findAllPieces();
        GameStatusDto gameStatusDto = boardDao.readCurrentTurn();

        Team currentTurn = Team.of(gameStatusDto.getCurrentTeam());
        return new Board(parsePieceInformation(boardDtos), currentTurn);
    }

    private Map<Position, Piece> parsePieceInformation(final List<BoardDto> boardDtos) {
        return boardDtos.stream()
                .collect(Collectors.toMap(dto -> Position.of(dto.getPosition()), dto -> Piece.of(dto.getPiece())));
    }

    public List<Position> findMovablePlaces(final Position start) throws SQLException {
        return tryFindMovablePositions(start);
    }

    private List<Position> tryFindMovablePositions(final Position start) throws SQLException {
        Board board = readBoard();

        try {
            checkGameOver(board);
            return board.findMovablePositions(start);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    private void checkGameOver(Board board) {
        Judge judge = new Judge();

        if (judge.findWinner(board).isPresent()) {
            throw new IllegalArgumentException("게임이 종료되었습니다.");
        }
    }

    public void move(final Position start, final Position end) throws SQLException {
        tryMove(start, end);
    }

    private void tryMove(final Position start, final Position end) throws SQLException {
        Board board = readBoard();

        try {
            checkGameOver(board);
            board.move(start, end);

            writeWholeBoard(board);
            writeCurrentTurn(board.getCurrentTurn());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    public double calculateScore(final Team team) throws SQLException {
        Judge judge = new Judge();
        return judge.getScoreByTeam(readBoard(), team);
    }
}