package dataAccess;

import com.google.gson.Gson;
import model.GameData;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

public class SQLGameDAO extends GameDAO {

    public SQLGameDAO () throws DataAccessException {
        configureDataBase();
    }

    public void insertGame(GameData game) throws DataAccessException {
        int gameID = game.getGameID();
        String blackUsername = game.getBlackUsername();
        String whiteUsername = game.getWhiteUsername();
        String gameName = game.getGameName();

        var insertString = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName) VALUES(?, ?, ?, ?)";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertString)) {
                preparedStatement.setInt(1, gameID);
                preparedStatement.setString(2, whiteUsername);
                preparedStatement.setString(3, blackUsername);
                preparedStatement.setString(4, gameName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to insert game: %s", ex.getMessage()));
        }
    }

    public GameData findGame(int gameID) throws DataAccessException {
        var insertString = "SELECT gameID FROM game WHERE gameID=?";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertString)) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"));
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to find game: %s", ex.getMessage()));
        }
    }

    public Collection<GameData> findAll() throws DataAccessException, SQLException {
        var gameList = new HashSet<GameData>();
        var insertString = "SELECT * FROM game";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertString)) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        GameData tempGame = new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"));
                        gameList.add(tempGame);
                    }
                    return gameList;
                }
            } catch (SQLException ex) {
                throw new DataAccessException(String.format("Unable to list games: %s", ex.getMessage()));
            }
        }
    }

    public void claimSpot(String userName, String color, Integer gameID) throws DataAccessException {

        try (var conn = DatabaseManager.getConnection()) {
            if (color.equalsIgnoreCase("WHITE")) {
                String insertString = "UPDATE game SET whiteUsername=? WHERE gameID=?";
                try (var preparedStatement = conn.prepareStatement(insertString)) {
                    preparedStatement.setString(1, userName);
                    preparedStatement.executeUpdate();
                }
            } else if (color.equalsIgnoreCase("BLACK")) {
                String insertString = "UPDATE game SET blackUsername=? WHERE gameID=?";
                try (var preparedStatement = conn.prepareStatement(insertString)) {
                    preparedStatement.setString(1, userName);
                    preparedStatement.executeUpdate();
                }
            } // Do we need to check for null color?
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to join game: %s", ex.getMessage()));
        }
    }

    public void clearTokens() throws DataAccessException {
        var insertString = "DELETE FROM game";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertString)) {
                preparedStatement.executeUpdate();
            }
        }  catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to clear data: %s", ex.getMessage()));
        }
    }

    private final String[] buildString = {
            """
            CREATE TABLE IF NOT EXISTS game (
            gameID INT NOT NULL,
            whiteUsername VARCHAR(255) NOT NULL,
            blackUsername VARCHAR(255) NOT NULL,
            gameName VARCHAR(255) NOT NULL,
            PRIMARY KEY (gameID)
            );
            """
    };

    private void configureDataBase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : buildString) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

}
