package service;
import dataAccess.*;

public class ClearApplicationService {

    public record ClearApplicationResult(String message) {}

    public static ClearApplicationResult clear() {
        try {
            new MemoryAuthTokenDAO().clearTokens();
            new MemoryUserDAO().clearTokens();
            new MemoryGameDAO().clearTokens();
            return new ClearApplicationResult("success");
        } catch (DataAccessException e) {
            return new ClearApplicationResult("error" + e.getMessage());
        }
    }

}
