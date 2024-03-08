package service;
import dataAccess.DataAccessException;
import model.AuthData;
import dataAccess.MemoryAuthTokenDAO;
import dataAccess.MemoryUserDAO;
import java.util.UUID;



public class LoginService {

    public record LoginRequest(String username, String password) {}

    public LoginService(MemoryUserDAO memoryUserDAO, MemoryAuthTokenDAO) {

    }

    public record LoginResult(String username, String authToken, String message) {}
    static MemoryUserDAO userMap = new MemoryUserDAO();
    static MemoryAuthTokenDAO authDAO = new MemoryAuthTokenDAO();

    public static LoginResult login(LoginRequest request) throws DataAccessException {
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