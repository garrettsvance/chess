package dataAccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException, SQLException {
        configureDataBase();
    }

    private String encrypt(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    public void insertUser(UserData userInfo) throws DataAccessException, SQLException {
        String userName = userInfo.getUserName();
        String encryptedPassword = encrypt(userInfo.getPassword());
        String email = userInfo.getEmail();
        var insertString = "INSERT INTO user (username, password, email) VALUES(?, ?, ?)";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertString)) {
                preparedStatement.setString(1, userName);
                preparedStatement.setString(2, encryptedPassword);
                preparedStatement.setString(3, email);
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                throw new DataAccessException(String.format("Unable to insert user: %s", ex.getMessage()));
            }
        }
    }

    public UserData verifyUser(UserData userInfo) throws DataAccessException {
        String userName = userInfo.getUserName();
        String password = userInfo.getPassword();
        var insertString = "SELECT username, password FROM user WHERE username=?";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertString)) {
                preparedStatement.setString(1, userName);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        String oldPassword = rs.getString("password");
                        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                        if (encoder.matches(password, oldPassword)) {
                            return userInfo;
                        }
                    }
                }
            }
        }  catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to verify user: %s", ex.getMessage()));
        }
        return null;
    }

    public UserData findUser(String userName) throws DataAccessException {
        var insertString = "SELECT username, password, email FROM user WHERE username=?";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertString)) {
                preparedStatement.setString(1, userName);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to find User: %s", ex.getMessage()));
        }
        return null;
    }

    public void clearTokens() throws DataAccessException {
        var insertString = "TRUNCATE user";
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
            CREATE TABLE IF NOT EXISTS user (
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL,
            PRIMARY KEY (username)
            );
            """
    };

    private void configureDataBase() throws SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : buildString) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new SQLException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

}
