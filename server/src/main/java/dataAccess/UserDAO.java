package dataAccess;

import model.UserData;

import java.sql.SQLException;

public interface UserDAO {


    void insertUser(UserData userInfo) throws DataAccessException, SQLException;

    UserData verifyUser(UserData userInfo) throws DataAccessException;

    UserData findUser(String userName) throws DataAccessException;

    void clearTokens() throws DataAccessException;

}
