package service;
import dataAccess.*;

public class ClearApplicationService {

    public record ClearApplicationResult(String message) {}

    public static ClearApplicationResult clear() {
        try {
            new AuthTokenDAO().clearTokens();
            new UserDAO().clearTokens();
            new GameDAO().clearTokens();
            return new ClearApplicationResult("success");
        } catch (DataAccessException e) {
            return new ClearApplicationResult("error" + e.getMessage());
        }
    }

}
