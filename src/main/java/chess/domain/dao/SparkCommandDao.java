package chess.domain.dao;

import chess.domain.dto.CommandDto;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SparkCommandDao {

    public SparkCommandDao() {
    }

    public void insert(CommandDto commandDto, Integer historyId) throws SQLException {
        String query = "INSERT INTO Command (data, history_id) VALUES (?, ?)";
        try (Connection connection = DriveManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, commandDto.data());
            preparedStatement.setInt(2, historyId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public List<CommandDto> selectAllCommands(String id) throws SQLException {
        List<CommandDto> commands = new ArrayList<>();
        String query = "SELECT * FROM history H JOIN Command C on H.history_id = C.history_id "
            + "WHERE H.history_id = ? AND H.is_end = false";

        try (Connection connection = DriveManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id);
            combineOneCommand(commands, preparedStatement);
        }
        return commands;
    }

    private void combineOneCommand(List<CommandDto> commands, PreparedStatement preparedStatement)
        throws SQLException {
        try (ResultSet rs = preparedStatement.executeQuery()) {
            insertOneCommand(commands, rs);
        }
    }

    private void insertOneCommand(List<CommandDto> commands, ResultSet rs) throws SQLException {
        while (rs.next()) {
            commands.add(new CommandDto(rs.getString("C.Data")));
        }
    }
}