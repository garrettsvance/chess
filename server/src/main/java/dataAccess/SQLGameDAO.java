package dataAccess;

import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public class SQLGameDAO extends GameDAO {

    public SQLGameDAO () throws DataAccessException {
        configureDataBase();
    }

    public void insertGame(GameData game) {}

    public GameData findGame(int gameID) {
        return null;
    }

    public Collection<GameData> findAll() {
        return null;
    }

    public void claimSpot(String userName, String color, Integer gameID) {}

    public void clearTokens() {}

    private final String[] buildStatement = { //TODO: figure out what primary key is, figure out proper statement
            """
            CREATE TABLE IF NOT EXISTS game (
            'game' JSON NOT NULL,
            'gameID' INT NOT NULL,
            'userName' VARCHAR(255) NOT NULL,
            'color' VARCHAR(255) NOT NULL,
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
