package serviceTests;
import dataAccess.AuthTokenDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import service.ClearApplicationService;
import service.CreateGameService;
import service.JoinGameService;
import service.ListGameService;
import service.LoginService;
import service.LogoutService;
import service.RegisterService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

public class AIOServiceTest {

    AuthTokenDAO authDAO;
    UserDAO userDAO;
    LoginService loginService;

    @Test
    @Order(1)
    @DisplayName("Clear Application Test 1")
    public void clearTest1() {
        ClearApplicationService.ClearApplicationResult result = ClearApplicationService.clear();
        Assertions.assertEquals("success", result.message());
    }

    @Test
    @Order(2)
    @DisplayName("Register User Test - Positive")
    public void registerGood() throws SQLException, DataAccessException {
        RegisterService.RegisterResult result = RegisterService.register(new RegisterService.RegisterRequest("garrett13", "password", "email"));
        Assertions.assertEquals("success", result.message());
    }

    @Test
    @Order(3)
    @DisplayName("Register User Test - Negative")
    public void registerBad() throws SQLException, DataAccessException {
        RegisterService.register(new RegisterService.RegisterRequest("garrett", "password", "email"));
        RegisterService.RegisterResult result = RegisterService.register(new RegisterService.RegisterRequest("garrett", "password", "email"));
        RegisterService.register(new RegisterService.RegisterRequest("garrett", "password", "email"));
        Assertions.assertEquals("Error: already taken", result.message());
    }

    @Test
    @Order(4)
    @DisplayName("Log In User Test - Positive")
    public void logInGood() throws DataAccessException, SQLException {
        RegisterService.register(new RegisterService.RegisterRequest("garrett11", "password", "email"));
        LoginService.LoginResult result = loginService.login(new LoginService.LoginRequest("garrett11", "password", "email"));
        Assertions.assertEquals("success", result.message());
    }

    @Test
    @Order(5)
    @DisplayName("Log In User Test - Negative")
    public void logInBad() throws DataAccessException {
        LoginService.LoginResult result = loginService.login(new LoginService.LoginRequest("garrett", "wrongpassword", "email"));
        Assertions.assertEquals("Error: unauthorized", result.message());
    }

    @Test
    @Order(6)
    @DisplayName("Log out User Test - Positive")
    public void logOutGood() throws DataAccessException, SQLException {
        RegisterService.register(new RegisterService.RegisterRequest("logouttest", "password", "email"));
        LoginService.LoginResult login = loginService.login(new LoginService.LoginRequest("logouttest", "password", "email"));
        String authToken = login.authToken();
        LogoutService.LogoutResult result = LogoutService.logout(authToken);
        Assertions.assertEquals("success", result.message());
    }

    @Test
    @Order(7)
    @DisplayName("Log Out User Test - Negative")
    public void logOutBad() throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        LogoutService.LogoutResult result = LogoutService.logout(authToken);
        Assertions.assertEquals("Error: unauthorized", result.message());
    }

    @Test
    @Order(8)
    @DisplayName("Create Game service Test - Positive")
    public void createGood() throws DataAccessException, SQLException {
        RegisterService.register(new RegisterService.RegisterRequest("creategametestgood", "password", "email"));
        LoginService.LoginResult login = loginService.login(new LoginService.LoginRequest("creategametestgood", "password", "email"));
        String authToken = login.authToken();
        CreateGameService.CreateGameResult result = CreateGameService.createGame(new CreateGameService.CreateGameRequest("game1"), authToken);
        Assertions.assertEquals("success", result.message());
    }


    @Test
    @Order(9)
    @DisplayName("Create Game Test - Negative")
    public void createBad() throws DataAccessException, SQLException {
        RegisterService.register(new RegisterService.RegisterRequest("creategametestbad", "password", "email"));
        LoginService.LoginResult login = loginService.login(new LoginService.LoginRequest("creategametestbad", "password", "email"));
        String authToken = login.authToken();
        CreateGameService.CreateGameResult result = CreateGameService.createGame(new CreateGameService.CreateGameRequest(null), authToken);
        Assertions.assertEquals("Error: bad request", result.message());
    }


    @Test
    @Order(10)
    @DisplayName("List All Games Test - Positive")
    public void listAllGood() throws SQLException, DataAccessException {
        RegisterService.register(new RegisterService.RegisterRequest("listgamestestgood", "password", "email"));
        LoginService.LoginResult login = loginService.login(new LoginService.LoginRequest("listgamestestgood", "password", "email"));
        String authToken = login.authToken();
        ListGameService.ListGamesResult result = ListGameService.listGames(authToken);
        Assertions.assertEquals("success", result.message());
    }


    @Test
    @Order(11)
    @DisplayName("List All Games Test - Negative")
    public void listAllBad() throws SQLException, DataAccessException {
        String authToken = UUID.randomUUID().toString();
        ListGameService.ListGamesResult result = ListGameService.listGames(authToken);
        Assertions.assertEquals("Error: unauthorized", result.message());
    }


    @Test
    @Order(12)
    @DisplayName("Join Game Test - Positive")
    public void joinGameGood() throws DataAccessException, SQLException {
        RegisterService.register(new RegisterService.RegisterRequest("joingametestgood", "password", "email"));
        LoginService.LoginResult login = loginService.login(new LoginService.LoginRequest("joingametestgood", "password", "email"));
        String authToken = login.authToken();
        CreateGameService.CreateGameResult create = CreateGameService.createGame(new CreateGameService.CreateGameRequest("game2"), authToken);
        Integer gameID = create.gameID();
        JoinGameService.JoinGameResult result = JoinGameService.joinGame(new JoinGameService.JoinGameRequest("White", gameID, authToken), authToken);
        Assertions.assertEquals("success", result.message());
    }


    @Test
    @Order(13)
    @DisplayName("Join Game Test - Negative")
    public void joinGameBad() throws DataAccessException, SQLException {
        RegisterService.register(new RegisterService.RegisterRequest("joingametestbad", "password", "email"));
        LoginService.LoginResult login = loginService.login(new LoginService.LoginRequest("joingametestbad", "password", "email"));
        String authToken = login.authToken();
        CreateGameService.CreateGameResult create = CreateGameService.createGame(new CreateGameService.CreateGameRequest("game3"), authToken);
        Integer gameID = create.gameID();
        JoinGameService.joinGame(new JoinGameService.JoinGameRequest("White", gameID, authToken), authToken);
        JoinGameService.JoinGameResult result = JoinGameService.joinGame(new JoinGameService.JoinGameRequest("White", gameID, authToken), authToken);
        Assertions.assertEquals("Error: already taken", result.message());
    }


    @Test
    @Order(14)
    @DisplayName("Clear Application Test 2")
    public void clearTest2() {
        ClearApplicationService.ClearApplicationResult result = ClearApplicationService.clear();
        Assertions.assertEquals("success", result.message());
    }

}