package service;
import model.AuthData;
import model.UserData;
import dataAccess.*;

import java.sql.SQLException;
import java.util.UUID;

public class RegisterService {

    private final UserDAO userDAO;
    private final AuthTokenDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthTokenDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResult(String username, String authToken, String message) {}

    public RegisterResult register(RegisterRequest request) throws DataAccessException, SQLException {
        if (request.email() == null || request.username() == null || request.password() == null) {
            return new RegisterResult(null, null, "Error: bad request");
        }
        if (userDAO.findUser(request.username()) != null) {
            return new RegisterResult(null, null, "Error: already taken");
        } else {
            userDAO.insertUser(new UserData(request.username(), request.password(), request.email()));
            String userToken = UUID.randomUUID().toString();
            authDAO.addToken(new AuthData(userToken, request.username()));
            return new RegisterResult(request.username(), userToken, "success");
        }
    }



}