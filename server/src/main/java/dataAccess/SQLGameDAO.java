package dataAccess;

import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {

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
        var insertString = "SELECT * FROM game WHERE gameID=?";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertString)) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to find game: %s", ex.getMessage()));
        }
        return null;
    }

    public Collection<GameData> findAll() throws DataAccessException, SQLException {
        var gameList = new ArrayList<GameData>();
        var insertString = "SELECT gameID, whiteUsername, blackUsername, gameName FROM game";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertString)) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        gameList.add(readGame(rs));
                    }
                }
            }
        }  catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to list games: %s", ex.getMessage()));
        }
        return gameList;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        // gamestate?
        return new GameData(gameID, whiteUsername, blackUsername, gameName);
    }


    public void claimSpot(String userName, String color, Integer gameID) throws DataAccessException {

        if (userName == null || findGame(gameID) == null) {
            throw new DataAccessException("Incorrect Game Information");
        }
        try (var conn = DatabaseManager.getConnection()) {
            if (color.equalsIgnoreCase("WHITE")) {
                String insertString = "UPDATE game SET whiteUsername=? WHERE gameID=?";
                try (var preparedStatement = conn.prepareStatement(insertString)) {
                    preparedStatement.setString(1, userName);
                    preparedStatement.setInt(2, gameID);
                    preparedStatement.executeUpdate();
                }
            } else if (color.equalsIgnoreCase("BLACK")) {
                String insertString = "UPDATE game SET blackUsername=? WHERE gameID=?";
                try (var preparedStatement = conn.prepareStatement(insertString)) {
                    preparedStatement.setString(1, userName);
                    preparedStatement.setInt(2, gameID);
                    preparedStatement.executeUpdate();
                }
            } // Do we need to check for null color?
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to join game: %s", ex.getMessage()));
        }
    }

    public void clearTokens() throws DataAccessException {
        var insertString = "TRUNCATE game";
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
            whiteUsername VARCHAR(255) DEFAULT NULL,
            blackUsername VARCHAR(255) DEFAULT NULL,
            gameName VARCHAR(255) NOT NULL,
            PRIMARY KEY (gameID)
            );
            """
    };

    private void configureDataBase() throws DataAccessException {
        SQLAuthTokenDAO.build(buildString);
    }

}
