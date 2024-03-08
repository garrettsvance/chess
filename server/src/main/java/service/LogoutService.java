package service;
import dataAccess.MemoryAuthTokenDAO;
import dataAccess.DataAccessException;

public class LogoutService {

    public record LogoutResult(String message) {}
    static MemoryAuthTokenDAO authDAO = new MemoryAuthTokenDAO();

    public static LogoutResult logout(String authToken) throws DataAccessException {

        if (authDAO.findToken(authToken) == null) {
            return new LogoutResult("Error: unauthorized");
        } else {
            authDAO.removeToken(authToken);
            return new LogoutResult("success");
        }
    }

}