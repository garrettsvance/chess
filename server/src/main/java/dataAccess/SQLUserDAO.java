package dataAccess;

import model.UserData;

import java.sql.SQLException;

public class SQLUserDAO extends UserDAO {

    public SQLUserDAO() throws DataAccessException {
        configureDataBase();
    }

    public void insertUser(UserData userInfo) {}

    public UserData findUser(String userName) {
        return null;
    }

    public void clearTokens() throws DataAccessException {}

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
