package service;
import SharedServices.LogoutResult;
import dataAccess.AuthTokenDAO;
import dataAccess.DataAccessException;

public class LogoutService {

    private final AuthTokenDAO authDAO;

    public LogoutService(AuthTokenDAO authDAO) {
        this.authDAO = authDAO;
    }


    public LogoutResult logout(String authToken) throws DataAccessException {

        if (authDAO.findToken(authToken) == null) {
            return new LogoutResult("Error: unauthorized");
        } else {
            authDAO.removeToken(authToken);
            return new LogoutResult("success");
        }
    }

}