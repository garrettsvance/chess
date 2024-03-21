package service;
import SharedServices.LoginRequest;
import SharedServices.LoginResult;
import dataAccess.*;
import model.AuthData;
import model.UserData;

import java.util.UUID;



public class LoginService {

    private final UserDAO userDAO;
    private final AuthTokenDAO authDAO;

    public LoginService(UserDAO userDAO, AuthTokenDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }


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