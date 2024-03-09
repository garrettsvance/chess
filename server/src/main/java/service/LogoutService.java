package service;
import dataAccess.AuthTokenDAO;
import dataAccess.MemoryAuthTokenDAO;
import dataAccess.DataAccessException;

public class LogoutService {

    private AuthTokenDAO authDAO;

    public LogoutService(AuthTokenDAO authDAO) {
        this.authDAO = authDAO;
    }

    public record LogoutResult(String message) {}
    //static MemoryAuthTokenDAO authDAO = new MemoryAuthTokenDAO();

    public LogoutResult logout(String authToken) throws DataAccessException {

        if (authDAO.findToken(authToken) == null) {
            return new LogoutResult("Error: unauthorized");
        } else {
            authDAO.removeToken(authToken);
            return new LogoutResult("success");
        }
    }

}