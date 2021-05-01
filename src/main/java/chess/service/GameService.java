package chess.service;

import chess.domain.game.Game;
import chess.domain.game.GameRepository;
import chess.domain.user.User;
import chess.domain.user.UserRepository;
import chess.web.dto.game.GameRequestDto;
import chess.web.dto.game.GameResponseDto;
import chess.web.dto.game.move.MoveCheckResponseDto;
import chess.web.dto.game.move.MoveRequestDto;
import chess.web.dto.game.move.MoveResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public GameService(final GameRepository gameRepository, final UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public long initializeGame(final GameRequestDto gameRequestDto) {
        return gameRepository.add(
            gameRequestDto.getName(),
            gameRequestDto.getHostId(),
            gameRequestDto.getGuestId()
        );
    }

    public GameResponseDto retrieveGameData(final long gameId) {
        final Game game = gameRepository.findById(gameId);
        final User host = userRepository.findById(game.getHostId());
        final User guest = userRepository.findById(game.getGuestId());
        return GameResponseDto.of(game, host, guest);
    }

    public MoveCheckResponseDto checkMovement(final long gameId,
        final MoveRequestDto moveRequestDto) {
        final Game game = gameRepository.findById(gameId);
        final boolean isMovable = game.checkMovement(
            moveRequestDto.getSource(), moveRequestDto.getTarget(), moveRequestDto.getColor()
        );
        return new MoveCheckResponseDto(isMovable);
    }

    public MoveResponseDto move(final long gameId, final MoveRequestDto moveRequestDto) {
        final Game game = gameRepository.findById(gameId);
        game.move(
            moveRequestDto.getSource(), moveRequestDto.getTarget(), moveRequestDto.getColor()
        );
        gameRepository.update(game);
        return new MoveResponseDto(game.isFinished());
    }

}