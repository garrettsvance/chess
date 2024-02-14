package service;
import dataAccess.*;

public class ClearApplicationService {

    public static ClearApplicationResult clear() {
        try {
            new authTokenDAO().clearTokens();
            new userDAO().clearTokens();
            new gameDAO().clearTokens();
            return new ClearApplicationResult("success");
        } catch (DataAccessException e) {
            return new ClearApplicationResult("error" + e.getMessage());
        }
    }

}
