package service;
import dataAccess.authTokenDAO;

public class LogoutService {

    public record LogoutResult(String message) {}
    static authTokenDAO authDAO = new authTokenDAO();

    public static LogoutResult logout(String authToken) {

        if (authDAO.findToken(authToken) == null) {
            return new LogoutResult("Error: unauthorized");
        } else {
            authDAO.removeToken(authToken);
            return new LogoutResult("success");
        }
    }

}