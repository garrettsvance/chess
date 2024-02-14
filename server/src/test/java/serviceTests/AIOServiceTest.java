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

public class AIOserviceTest {

    @Test
    @Order(1)
    @DisplayName("Clear Application Test 1")
    public void clearTest1() {
        ClearApplicationService.ClearApplicationResult result = ClearApplicationService.clear();
        Assertions.assertNull(result.message());
    }

    @Test
    @Order(2)
    @DisplayName("Register User Test - Positive")
    public void registerGood() {
        RegisterService.RegisterResult result = RegisterService.register(new RegisterService.RegisterRequest("garrett13", "password", "email"));
        Assertions.assertNull(result.message());
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
        Assertions.assertNull(result.message());
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
        service.Registerservice.register(new RegisterRequest("logouttest", "password", "email"));
        Result.LoginResult login = service.Loginservice.login(new LoginRequest("logouttest", "password"));
        String authToken = login.getAuthToken();
        Result.LogoutResult result = service.Logoutservice.logout(authToken);
        Assertions.assertNull(result.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("Log Out User Test - Negative")
    public void logOutBad() {
        String authToken = UUID.randomUUID().toString();
        Result.LogoutResult result = service.Logoutservice.logout(authToken);
        Assertions.assertEquals("Error: unauthorized", result.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Create Game service Test - Positive")
    public void createGood() {
        service.Registerservice.register(new RegisterRequest("creategametestgood", "password", "email"));
        Result.LoginResult login = service.Loginservice.login(new LoginRequest("creategametestgood", "password"));
        String authToken = login.getAuthToken();
        Result.CreateGameResult result = service.CreateGameservice.createGame(new CreateGameRequest("game1"), authToken);
        Assertions.assertEquals("gameID", result.getMessage());
    }


    @Test
    @Order(9)
    @DisplayName("Create Game Test - Negative")
    public void createBad() {
        service.Registerservice.register(new RegisterRequest("creategametestbad", "password", "email"));
        Result.LoginResult login = service.Loginservice.login(new LoginRequest("creategametestbad", "password"));
        String authToken = login.getAuthToken();
        Result.CreateGameResult result = service.CreateGameservice.createGame(new CreateGameRequest(null), authToken);
        Assertions.assertEquals("Error: bad request", result.getMessage());
    }


    @Test
    @Order(10)
    @DisplayName("List All Games Test - Positive")
    public void listAllGood() {
        service.Registerservice.register(new RegisterRequest("listgamestestgood", "password", "email"));
        Result.LoginResult login = service.Loginservice.login(new LoginRequest("listgamestestgood", "password"));
        String authToken = login.getAuthToken();
        Result.ListGamesResult result = service.ListGameservice.listGames(authToken);
        Assertions.assertNull(result.getMessage());
    }


    @Test
    @Order(11)
    @DisplayName("List All Games Test - Negative")
    public void listAllBad() {
        String authToken = UUID.randomUUID().toString();
        Result.ListGamesResult result = service.ListGameservice.listGames(authToken);
        Assertions.assertEquals("Error: unauthorized", result.getMessage());
    }


    @Test
    @Order(12)
    @DisplayName("Join Game Test - Positive")
    public void joinGameGood() {
        service.Registerservice.register(new RegisterRequest("joingametestgood", "password", "email"));
        Result.LoginResult login = service.Loginservice.login(new LoginRequest("joingametestgood", "password"));
        String authToken = login.getAuthToken();
        Result.CreateGameResult create = service.CreateGameservice.createGame(new CreateGameRequest("game2"), authToken);
        Integer gameID = create.getGameID();
        Result.JoinGameResult result = service.JoinGameservice.joinGame(new JoinGameRequest("White", gameID, authToken), authToken);
        Assertions.assertNull(result.getMessage());
    }


    @Test
    @Order(13)
    @DisplayName("Join Game Test - Negative")
    public void joinGameBad() {
        service.Registerservice.register(new RegisterRequest("joingametestbad", "password", "email"));
        Result.LoginResult login = service.Loginservice.login(new LoginRequest("joingametestbad", "password"));
        String authToken = login.getAuthToken();
        Result.CreateGameResult create = service.CreateGameservice.createGame(new CreateGameRequest("game3"), authToken);
        Integer gameID = create.getGameID();
        service.JoinGameservice.joinGame(new JoinGameRequest("White", gameID, authToken), authToken);
        Result.JoinGameResult result = service.JoinGameservice.joinGame(new JoinGameRequest("White", gameID, authToken), authToken);
        Assertions.assertEquals("Error: already taken", result.getMessage());
    }


    @Test
    @Order(14)
    @DisplayName("Clear Application Test 2")
    public void clearTest2() {
        ClearApplicationResult result = service.ClearApplicationservice.clear();
        Assertions.assertNull(result.getMessage());
    }

}