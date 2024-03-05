package dataAccess;

import model.AuthData;

import java.sql.SQLException;

public class SQLAuthTokenDAO extends AuthTokenDAO { //TODO: extends vs implements

    public SQLAuthTokenDAO() throws DataAccessException {
        configureDataBase();
    }

    public void addToken(AuthData authToken) {}

    public AuthData findToken(String authToken) {
        return null;
    }

    public void removeToken(String authToken) {}

    public void clearTokens() throws DataAccessException {}

    private final String[] buildStatement = {
            """
            CREATE TABLE IF NOT EXISTS auth (
            'authToken' VARCHAR(100) NOT NULL,
            PRIMARY KEY ('authToken')
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
