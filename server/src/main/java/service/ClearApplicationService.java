package service;
import dataAccess.*;

public class ClearApplicationService {

    private UserDAO userDAO;
    private AuthTokenDAO authDAO;
    private GameDAO gameDAO;

    public ClearApplicationService(UserDAO userDAO, AuthTokenDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public record ClearApplicationResult(String message) {}

    public ClearApplicationResult clear() {
        try {
            userDAO.clearTokens();
            authDAO.clearTokens();
            gameDAO.clearTokens();
            return new ClearApplicationResult("success");
        } catch (DataAccessException e) {
            return new ClearApplicationResult("error" + e.getMessage());
        }
    }

}
