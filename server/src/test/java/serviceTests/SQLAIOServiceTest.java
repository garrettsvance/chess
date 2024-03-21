package serviceTests;
import SharedServices.*;
import dataAccess.*;
import org.junit.jupiter.api.*;
import service.ClearApplicationService;
import service.CreateGameService;
import service.JoinGameService;
import service.ListGameService;
import service.LoginService;
import service.LogoutService;
import service.RegisterService;

import java.sql.SQLException;
import java.util.UUID;

public class SQLAIOServiceTest {

    AuthTokenDAO authDAO;
    UserDAO userDAO;
    GameDAO gameDAO;
    LoginService loginService;
    RegisterService registerService;
    ClearApplicationService clearApplicationService;
    LogoutService logoutService;
    ListGameService listGameService;
    CreateGameService createGameService;
    JoinGameService joinGameService;

    @BeforeEach
    public void setUp() throws DataAccessException, SQLException {
        authDAO = new SQLAuthTokenDAO();
        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();

        loginService = new LoginService(userDAO, authDAO);
        registerService = new RegisterService(userDAO, authDAO);
        clearApplicationService = new ClearApplicationService(userDAO, authDAO, gameDAO);
        logoutService = new LogoutService(authDAO);
        listGameService = new ListGameService(authDAO, gameDAO);
        createGameService = new CreateGameService(authDAO, gameDAO);
        joinGameService = new JoinGameService(authDAO, gameDAO);

        clearApplicationService.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Clear Application Test 1")
    public void clearTest1() {
        ClearApplicationResult result = clearApplicationService.clear();
        Assertions.assertEquals("success", result.message());
    }

    @Test
    @Order(2)
    @DisplayName("Register User Test - Positive")
    public void registerGood() throws SQLException, DataAccessException {
        RegisterResult result = registerService.register(new RegisterRequest("garrett13", "password", "email"));
        Assertions.assertEquals("success", result.message());
    }

    @Test
    @Order(3)
    @DisplayName("Register User Test - Negative")
    public void registerBad() throws SQLException, DataAccessException {
        registerService.register(new RegisterRequest("garrett", "password", "email"));
        RegisterResult result = registerService.register(new RegisterRequest("garrett", "password", "email"));
        registerService.register(new RegisterRequest("garrett", "password", "email"));
        Assertions.assertEquals("Error: already taken", result.message());
    }

    @Test
    @Order(4)
    @DisplayName("Log In User Test - Positive")
    public void logInGood() throws DataAccessException, SQLException {
        registerService.register(new RegisterRequest("garrett11", "password", "email"));
        LoginResult result = loginService.login(new LoginRequest("garrett11", "password", "email"));
        Assertions.assertEquals("success", result.message());
    }

    @Test
    @Order(5)
    @DisplayName("Log In User Test - Negative")
    public void logInBad() throws DataAccessException {
        LoginResult result = loginService.login(new LoginRequest("garrett", "wrongpassword", "email"));
        Assertions.assertEquals("Error: unauthorized", result.message());
    }

    @Test
    @Order(6)
    @DisplayName("Log out User Test - Positive")
    public void logOutGood() throws DataAccessException, SQLException {
        registerService.register(new RegisterRequest("logouttest", "password", "email"));
        LoginResult login = loginService.login(new LoginRequest("logouttest", "password", "email"));
        String authToken = login.authToken();
        LogoutResult result = logoutService.logout(authToken);
        Assertions.assertEquals("success", result.message());
    }

    @Test
    @Order(7)
    @DisplayName("Log Out User Test - Negative")
    public void logOutBad() throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        LogoutResult result = logoutService.logout(authToken);
        Assertions.assertEquals("Error: unauthorized", result.message());
    }

    @Test
    @Order(8)
    @DisplayName("Create Game service Test - Positive")
    public void createGood() throws DataAccessException, SQLException {
        registerService.register(new RegisterRequest("creategametestgood", "password", "email"));
        LoginResult login = loginService.login(new LoginRequest("creategametestgood", "password", "email"));
        String authToken = login.authToken();
        CreateGameResult result = createGameService.createGame(new CreateGameRequest("game1", null, null), authToken);
        Assertions.assertEquals("success", result.message());
    }


    @Test
    @Order(9)
    @DisplayName("Create Game Test - Negative")
    public void createBad() throws DataAccessException, SQLException {
        registerService.register(new RegisterRequest("creategametestbad", "password", "email"));
        LoginResult login = loginService.login(new LoginRequest("creategametestbad", "password", "email"));
        String authToken = login.authToken();
        CreateGameResult result = createGameService.createGame(new CreateGameRequest(null, null, null), authToken);
        Assertions.assertEquals("Error: bad request", result.message());
    }


    @Test
    @Order(10)
    @DisplayName("List All Games Test - Positive")
    public void listAllGood() throws SQLException, DataAccessException {
        registerService.register(new RegisterRequest("listgamestestgood", "password", "email"));
        LoginResult login = loginService.login(new LoginRequest("listgamestestgood", "password", "email"));
        String authToken = login.authToken();
        ListGamesResult result = listGameService.listGames(authToken);
        Assertions.assertEquals("success", result.message());
    }


    @Test
    @Order(11)
    @DisplayName("List All Games Test - Negative")
    public void listAllBad() throws SQLException, DataAccessException {
        String authToken = UUID.randomUUID().toString();
        ListGamesResult result = listGameService.listGames(authToken);
        Assertions.assertEquals("Error: unauthorized", result.message());
    }


    @Test
    @Order(12)
    @DisplayName("Join Game Test - Positive")
    public void joinGameGood() throws DataAccessException, SQLException {
        registerService.register(new RegisterRequest("joingametestgood", "password", "email"));
        LoginResult login = loginService.login(new LoginRequest("joingametestgood", "password", "email"));
        String authToken = login.authToken();
        CreateGameResult create = createGameService.createGame(new CreateGameRequest("game2", null, null), authToken);
        Integer gameID = create.game().getGameID();
        JoinGameResult result = joinGameService.joinGame(new JoinGameRequest("White", gameID, authToken), authToken);
        Assertions.assertEquals("success", result.message());
    }


    @Test
    @Order(13)
    @DisplayName("Join Game Test - Negative")
    public void joinGameBad() throws DataAccessException, SQLException {
        registerService.register(new RegisterRequest("joingametestbad", "password", "email"));
        LoginResult login = loginService.login(new LoginRequest("joingametestbad", "password", "email"));
        String authToken = login.authToken();
        CreateGameResult create = createGameService.createGame(new CreateGameRequest("game3", null, null), authToken);
        Integer gameID = create.game().getGameID();
        joinGameService.joinGame(new JoinGameRequest("White", gameID, authToken), authToken);
        JoinGameResult result = joinGameService.joinGame(new JoinGameRequest("White", gameID, authToken), authToken);
        Assertions.assertEquals("Error: already taken", result.message());
    }


    @Test
    @Order(14)
    @DisplayName("Clear Application Test 2")
    public void clearTest2() {
        ClearApplicationResult result = clearApplicationService.clear();
        Assertions.assertEquals("success", result.message());
    }

}