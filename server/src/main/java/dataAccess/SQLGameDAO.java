package dataAccess;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
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

        //TODO: double check the insert methods, how that works, whether or not you're inserting "INTO" the correct place
        var insertString = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName)" + "VALUES (\"" + gameID + "\", \"" + whiteUsername + "\", \"" + blackUsername + "\", \"" + gameName + "\")";

        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(insertString)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to insert game: %s", ex.getMessage()));
        }
    }

    public GameData findGame(int gameID) throws DataAccessException {
        //TODO: again, check the "FROM"
        var insertString = "SELECT gameID FROM game where gameID = \"" + gameID + "\"" ;

        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(insertString)) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"));
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to find game: %s", ex.getMessage()));
        }
    }

    public Collection<GameData> findAll() throws DataAccessException, SQLException {
        var gameList = new HashSet<GameData>();
        var insertString = "SELECT * FROM game";
        String gameDataJson;

        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(insertString)) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        gameDataJson = rs.getString("gameDataJson");
                        gameList.add(new Gson().fromJson(gameDataJson, GameData.class));
                    }
                    return gameList;
                }
            } catch (SQLException ex) {
                throw new DataAccessException(String.format("Unable to list games: %s", ex.getMessage()));
            }
        }
    }

    public void claimSpot(String userName, String color, Integer gameID) {}

    public void clearTokens() {}

    private final String[] buildStatement = { //TODO: figure out what primary key is, figure out proper statement
            """
            CREATE TABLE IF NOT EXISTS game (
            'game' JSON NOT NULL,
            'gameID' INT NOT NULL,
            'userName' VARCHAR(100) NOT NULL,
            'color' VARCHAR(100) NOT NULL,
            PRIMARY KEY ('game')
            );
            """
    };

    private void configureDataBase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : buildStatement) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

}
