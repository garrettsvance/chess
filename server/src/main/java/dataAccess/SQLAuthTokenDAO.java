package dataAccess;

import model.AuthData;

import java.sql.SQLException;

public class SQLAuthTokenDAO extends AuthTokenDAO { //TODO: extends vs implements

    public SQLAuthTokenDAO() throws DataAccessException {
        configureDataBase();
    }

    public void addToken(AuthData authToken) throws DataAccessException {
        //TODO: does this need to be formatted into a string?
        var insertString = "INSERT INTO auth " + authToken;

        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(insertString)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to add token: %s", ex.getMessage()));
        }

    }

    public AuthData findToken(String authToken) throws DataAccessException {
        var insertString = "SELECT authToken FROM auth WHERE authToken = " + authToken;

        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(insertString)) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                return new AuthData(rs.getString("authToken"), rs.getString("userName"));
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to find token: %s", ex.getMessage()));
        }
    }

    public void removeToken(String authToken) throws DataAccessException {
        var insertString = "DELETE authToken FROM auth WHERE authToken = " + authToken;

        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(insertString)) {
                preparedStatement.executeQuery();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to find token: %s", ex.getMessage()));
        }
    }

    public void clearTokens() throws DataAccessException {
        var insertString = "DELETE FROM authToken";
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(insertString)) {
                preparedStatement.executeUpdate();
            }
        }  catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to clear data: %s", ex.getMessage()));
        }
    }

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
