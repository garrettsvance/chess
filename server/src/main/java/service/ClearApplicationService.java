package service;

public class ClearApplicationService {

    public static ClearApplicationResult clear() {
        try {
            new authTokenDAO().clearTokens();
            new userDAO
        }
    }

}
