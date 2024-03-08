package service;
import dataAccess.AuthTokenDAO;
import dataAccess.DataAccessException;

public class LogoutService {

    public record LogoutResult(String message) {}
    static AuthTokenDAO authDAO = new AuthTokenDAO();

    public static LogoutResult logout(String authToken) throws DataAccessException {

        if (authDAO.findToken(authToken) == null) {
            return new LogoutResult("Error: unauthorized");
        } else {
            authDAO.removeToken(authToken);
            return new LogoutResult("success");
        }
    }

}