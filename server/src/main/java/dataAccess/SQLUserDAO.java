package dataAccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public class SQLUserDAO extends UserDAO {

    public SQLUserDAO() throws DataAccessException, SQLException {
        configureDataBase();
    }

    private String encrypt(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    private boolean checkPassword(String oldPassword, String newPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(newPassword, oldPassword);
    }


    public void insertUser(UserData userInfo) throws DataAccessException, SQLException {
        String userName = userInfo.getUserName();
        String encryptedPassword = encrypt(userInfo.getPassword());
        String email = userInfo.getEmail();
        var insertString = "INSERT INTO user (username, password, email)" + "VALUES (\"" + userName + "\", \"" + encryptedPassword + "\", \"" + email + "\")";

        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(insertString)) {
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                throw new DataAccessException(String.format("Unable to insert User: %s", ex.getMessage()));
            }
        }
    }

    public UserData findUser(String userName) throws DataAccessException {
        var insertString = "SELECT username FROM user where username = \"" + userName + "\"" ;

        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(insertString)) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to find User: %s", ex.getMessage()));
        }
    }



    public void clearTokens() throws DataAccessException {
        var insertString = "DELETE FROM user";
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(insertString)) {
                preparedStatement.executeUpdate();
            }
        }  catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to clear data: %s", ex.getMessage()));
        }
    }

    private final String[] buildStatement = { //TODO: figure out what primary key is, figure out proper statement
            """
            CREATE TABLE IF NOT EXISTS (
            PRIMARY KEY ('')
            );
            """
    };

    private void configureDataBase() throws SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : buildStatement) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new SQLException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

}
