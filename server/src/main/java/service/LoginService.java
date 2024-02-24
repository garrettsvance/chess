package service;
import model.AuthData;
import dataAccess.AuthTokenDAO;
import dataAccess.UserDAO;
import java.util.UUID;



public class LoginService {

    public record LoginRequest(String username, String password) {}
    public record LoginResult(String username, String authToken, String message) {}
    static UserDAO userMap = new UserDAO();
    static AuthTokenDAO authDAO = new AuthTokenDAO();

    public static LoginResult login(LoginRequest request) {
        String userName = request.username();
        String passWord = request.password();

        if (userMap.findUser(userName) == null || !passWord.equals(userMap.findUser(userName).getPassword())) {
            return new LoginResult(null, null, "Error: unauthorized");
        } else {
            String userToken = UUID.randomUUID().toString();
            authDAO.addToken(new AuthData(userToken, userName));
            return new LoginResult(userName, userToken, "success");
        }
    }

}