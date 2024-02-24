package service;
import dataAccess.AuthTokenDAO;

public class LogoutService {

    public record LogoutResult(String message) {}
    static AuthTokenDAO authDAO = new AuthTokenDAO();

    public static LogoutResult logout(String authToken) {

        if (authDAO.findToken(authToken) == null) {
            return new LogoutResult("Error: unauthorized");
        } else {
            authDAO.removeToken(authToken);
            return new LogoutResult("success");
        }
    }

}