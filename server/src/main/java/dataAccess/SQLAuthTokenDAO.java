package dataAccess;

import model.AuthData;

import java.sql.SQLException;

public class SQLAuthTokenDAO extends AuthTokenDAO { //TODO: extends vs implements

    public SQLAuthTokenDAO() throws DataAccessException {
        configureDataBase();
    }

    public void addToken(AuthData authToken) throws DataAccessException {
        String authtoken = authToken.getAuthToken();
        String username = authToken.getUserName();
        var insertString = "INSERT INTO auth (authToken, username) VALUES (?, ?)";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertString)) {
                preparedStatement.setString(1, authtoken);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to add token: %s", ex.getMessage()));
        }

    }

    public AuthData findToken(String authToken) throws DataAccessException {
        var insertString = "SELECT authToken FROM auth WHERE authToken=?";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertString)) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to find token: %s", ex.getMessage()));
        }
    }

    public void removeToken(String authToken) throws DataAccessException {
        var insertString = "DELETE authToken FROM auth WHERE authToken=?";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertString)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to find token: %s", ex.getMessage()));
        }
    }

    public void clearTokens() throws DataAccessException {
        var insertString = "DELETE FROM authToken";
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
            CREATE TABLE IF NOT EXISTS auth (
            authToken VARCHAR(255) NOT NULL,
            username VARCHAR(255) NOT NULL,
            PRIMARY KEY ('authToken')
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
