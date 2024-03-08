package dataAccess;

import model.UserData;

import java.sql.SQLException;

public interface UserDAO {

    String encrypt(String password);

    boolean checkPassword(String oldPassword, String newPassword);

    void insertUser(UserData userInfo) throws DataAccessException, SQLException;

    UserData verifyUser(UserData userInfo) throws DataAccessException;

    UserData findUser(String userName) throws DataAccessException;

    void clearTokens() throws DataAccessException;

}
