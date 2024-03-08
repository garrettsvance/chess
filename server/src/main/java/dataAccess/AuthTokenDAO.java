package dataAccess;

import model.AuthData;

public interface AuthTokenDAO {

    void addToken(AuthData authToken) throws DataAccessException;

    AuthData findToken(String authToken) throws DataAccessException;

    void removeToken(String authToken) throws DataAccessException;

    void clearTokens() throws DataAccessException;

}
