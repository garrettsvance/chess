package serviceTests;
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

import java.util.UUID;

public class AIOServiceTest {

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
    public void registerGood() {
        RegisterService.RegisterResult result = RegisterService.register(new RegisterService.RegisterRequest("garrett13", "password", "email"));
        Assertions.assertEquals("success", result.message());
    }

    @Test
    @Order(3)
    @DisplayName("Register User Test - Negative")
    public void registerBad() {
        RegisterService.register(new RegisterService.RegisterRequest("garrett", "password", "email"));
        RegisterService.RegisterResult result = RegisterService.register(new RegisterService.RegisterRequest("garrett", "password", "email"));
        RegisterService.register(new RegisterService.RegisterRequest("garrett", "password", "email"));
        Assertions.assertEquals("Error: already taken", result.message());
    }

    @Test
    @Order(4)
    @DisplayName("Log In User Test - Positive")
    public void logInGood() {
        RegisterService.register(new RegisterService.RegisterRequest("garrett11", "password", "email"));
        LoginService.LoginResult result = LoginService.login(new LoginService.LoginRequest("garrett11", "password"));
        Assertions.assertEquals("success", result.message());
    }

    @Test
    @Order(5)
    @DisplayName("Log In User Test - Negative")
    public void logInBad() {
        LoginService.LoginResult result = LoginService.login(new LoginService.LoginRequest("garrett", "wrongpassword"));
        Assertions.assertEquals("Error: unauthorized", result.message());
    }

    @Test
    @Order(6)
    @DisplayName("Log out User Test - Positive")
    public void logOutGood() {
        RegisterService.register(new RegisterService.RegisterRequest("logouttest", "password", "email"));
        LoginService.LoginResult login = LoginService.login(new LoginService.LoginRequest("logouttest", "password"));
        String authToken = login.authToken();
        LogoutService.LogoutResult result = LogoutService.logout(authToken);
        Assertions.assertEquals("success", result.message());
    }

    @Test
    @Order(7)
    @DisplayName("Log Out User Test - Negative")
    public void logOutBad() {
        String authToken = UUID.randomUUID().toString();
        LogoutService.LogoutResult result = LogoutService.logout(authToken);
        Assertions.assertEquals("Error: unauthorized", result.message());
    }

    @Test
    @Order(8)
    @DisplayName("Create Game service Test - Positive")
    public void createGood() {
        RegisterService.register(new RegisterService.RegisterRequest("creategametestgood", "password", "email"));
        LoginService.LoginResult login = LoginService.login(new LoginService.LoginRequest("creategametestgood", "password"));
        String authToken = login.authToken();
        CreateGameService.CreateGameResult result = CreateGameService.createGame(new CreateGameService.CreateGameRequest("game1"), authToken);
        Assertions.assertEquals("gameID", result.message());
    }


    @Test
    @Order(9)
    @DisplayName("Create Game Test - Negative")
    public void createBad() {
        RegisterService.register(new RegisterService.RegisterRequest("creategametestbad", "password", "email"));
        LoginService.LoginResult login = LoginService.login(new LoginService.LoginRequest("creategametestbad", "password"));
        String authToken = login.authToken();
        CreateGameService.CreateGameResult result = CreateGameService.createGame(new CreateGameService.CreateGameRequest(null), authToken);
        Assertions.assertEquals("Error: bad request", result.message());
    }


    @Test
    @Order(10)
    @DisplayName("List All Games Test - Positive")
    public void listAllGood() {
        RegisterService.register(new RegisterService.RegisterRequest("listgamestestgood", "password", "email"));
        LoginService.LoginResult login = LoginService.login(new LoginService.LoginRequest("listgamestestgood", "password"));
        String authToken = login.authToken();
        ListGameService.ListGamesResult result = ListGameService.listGames(authToken);
        Assertions.assertEquals("success", result.message());
    }


    @Test
    @Order(11)
    @DisplayName("List All Games Test - Negative")
    public void listAllBad() {
        String authToken = UUID.randomUUID().toString();
        ListGameService.ListGamesResult result = ListGameService.listGames(authToken);
        Assertions.assertEquals("Error: unauthorized", result.message());
    }


    @Test
    @Order(12)
    @DisplayName("Join Game Test - Positive")
    public void joinGameGood() {
        RegisterService.register(new RegisterService.RegisterRequest("joingametestgood", "password", "email"));
        LoginService.LoginResult login = LoginService.login(new LoginService.LoginRequest("joingametestgood", "password"));
        String authToken = login.authToken();
        CreateGameService.CreateGameResult create = CreateGameService.createGame(new CreateGameService.CreateGameRequest("game2"), authToken);
        Integer gameID = create.gameID();
        JoinGameService.JoinGameResult result = JoinGameService.joinGame(new JoinGameService.JoinGameRequest("White", gameID, authToken), authToken);
        Assertions.assertEquals("success", result.message());
    }


    @Test
    @Order(13)
    @DisplayName("Join Game Test - Negative")
    public void joinGameBad() {
        RegisterService.register(new RegisterService.RegisterRequest("joingametestbad", "password", "email"));
        LoginService.LoginResult login = LoginService.login(new LoginService.LoginRequest("joingametestbad", "password"));
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