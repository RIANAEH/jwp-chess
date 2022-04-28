package chess.dao;

import chess.domain.chesspiece.ChessPiece;
import chess.domain.position.Position;
import chess.dto.ChessPieceMapper;
import chess.entity.PieceEntity;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class PieceDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;


    public PieceDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("pieces")
                .usingGeneratedKeyColumns("id");
    }

    public void saveAllByRoomId(final Long roomId, final Map<Position, ChessPiece> pieces) {
        final List<PieceEntity> pieceEntities = pieces.entrySet()
                .stream()
                .map(entry -> new PieceEntity(roomId, entry.getKey().getValue(),
                        ChessPieceMapper.toPieceType(entry.getValue()), entry.getValue().color().getValue()))
                .collect(Collectors.toList());

        insertActor.executeBatch(SqlParameterSourceUtils.createBatch(pieceEntities));
    }

    public List<PieceEntity> findAllByRoomId(final Long roomId) {
        final String sql = "select * from pieces where room_id = ?";
        return jdbcTemplate.query(sql, rowMapper(), roomId);
    }

    private RowMapper<PieceEntity> rowMapper() {
        return (resultSet, rowNum) -> new PieceEntity(
                resultSet.getLong("id"),
                resultSet.getLong("room_id"),
                resultSet.getString("position"),
                resultSet.getString("type"),
                resultSet.getString("color")
        );
    }

    public PieceEntity findByRoomIdAndPosition(final Long roomId, final String position) {
        final String sql = "select * from pieces where room_id = ? and position = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper(), roomId, position);
    }

    public void updatePositionByRoomId(final Long roomId, final String from, final String to) {
        final String sql = "update pieces set position = ? where room_id = ? and position = ?";
        jdbcTemplate.update(sql, to, roomId, from);
    }

    public void deleteByRoomIdAndPosition(final Long roomId, final String position) {
        final String sql = "delete from pieces where room_id = ? and position = ?";
        jdbcTemplate.update(sql, roomId, position);
    }
}