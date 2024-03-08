package service;
import dataAccess.*;
import model.AuthData;
import model.UserData;

import java.util.UUID;



public class LoginService {

    private UserDAO userDAO;
    private AuthTokenDAO authDAO;

    public LoginService(UserDAO userDAO, AuthTokenDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public record LoginRequest(String username, String password, String email) {}

    public record LoginResult(String username, String authToken, String message) {}
    //static MemoryUserDAO userMap = new MemoryUserDAO();
    //static MemoryAuthTokenDAO authDAO = new MemoryAuthTokenDAO();

    public LoginResult login(LoginRequest request) throws DataAccessException {
        String userName = request.username();
        String passWord = request.password();
        String email = request.email();
        UserData userInfo = new UserData(userName, passWord, email);

        if (userDAO.verifyUser(userInfo) == null) {
            /*userDAO.findUser(userName) == null || !userDAO.!passWord.equals(userMap.findUser(userName).getPassword())*/
            return new LoginResult(null, null, "Error: unauthorized");
        } else {
            String userToken = UUID.randomUUID().toString();
            authDAO.addToken(new AuthData(userToken, userName));
            return new LoginResult(userName, userToken, "success");
        }
    }

}