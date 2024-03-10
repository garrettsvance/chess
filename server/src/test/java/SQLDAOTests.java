import dataAccess.*;
import dataAccess.MemoryAuthTokenDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import passoffTests.testClasses.TestModels;
import service.ClearApplicationService;
import service.CreateGameService;
import service.JoinGameService;
import service.ListGameService;
import service.LoginService;
import service.LogoutService;
import service.RegisterService;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class SQLDAOTests {

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
        DatabaseManager.createDatabase();

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

    /* Begin AuthDAO tests */

    @Test
    @Order(1)
    @DisplayName("AddToken Test - Positive")
    public void addTokenGood() throws DataAccessException {
        String auth = "auth";
        String username = "username";
        var authToken = new AuthData(auth, username);

        authDAO.addToken(authToken);
        var result = authDAO.findToken("auth");
        Assertions.assertEquals(authToken.getAuthToken(), result.getAuthToken());
    }

    @Test
    @Order(2)
    @DisplayName("AddToken Test - Negative")
    public void addTokenBad() throws DataAccessException {
        var result = authDAO.findToken("auth");
        Assertions.assertNull(result);
    }

    @Test
    @Order(3)
    @DisplayName("FindToken Test - Positive")
    public void findTokenGood() throws DataAccessException {
        String auth = "auth";
        String username = "username";
        var authToken = new AuthData(auth, username);

        authDAO.addToken(authToken);
        var result = authDAO.findToken("auth");
        Assertions.assertEquals(authToken.getAuthToken(), result.getAuthToken());
    }

    @Test
    @Order(4)
    @DisplayName("FindToken Test - Negative")
    public void findTokenBad() throws DataAccessException {
        String auth = "auth";
        String username = "username";
        var authToken = new AuthData(auth, username);

        authDAO.addToken(authToken);
        var result = authDAO.findToken("authfalse");
        Assertions.assertNull(result);
    }

    @Test
    @Order(5)
    @DisplayName("RemoveToken Test - Positive")
    public void removeTokenGood() throws DataAccessException {
        String auth = "auth";
        String username = "username";
        var authToken = new AuthData(auth, username);

        authDAO.addToken(authToken);
        var result = authDAO.findToken("auth");
        Assertions.assertEquals(authToken.getAuthToken(), result.getAuthToken());

        authDAO.removeToken("auth");
        Assertions.assertNull(authDAO.findToken("auth"));
    }

    @Test
    @Order(6)
    @DisplayName("RemoveToken Test - Negative")
    public void removeTokenBad() throws DataAccessException {
        String auth = "auth";
        String username = "username";
        var authToken = new AuthData(auth, username);

        authDAO.addToken(authToken);
        var result = authDAO.findToken("auth");
        Assertions.assertEquals(authToken.getAuthToken(), result.getAuthToken());

        authDAO.removeToken("auth2");
        Assertions.assertEquals(authToken.getAuthToken(), result.getAuthToken());
    }

    @Test
    @Order(7)
    @DisplayName("AuthDAO ClearTokens Test - Positive")
    public void authClearTokens() throws DataAccessException {
        String auth = "auth";
        String username = "username";
        var authToken = new AuthData(auth, username);

        authDAO.addToken(authToken);
        var result = authDAO.findToken("auth");
        Assertions.assertEquals(authToken.getAuthToken(), result.getAuthToken());

        authDAO.clearTokens();
        Assertions.assertNull(authDAO.findToken("auth"));
    }

    /* Begin GameDAO Tests  */

    @Test
    @Order(8)
    @DisplayName("InsertGame Test - Positive")
    public void insertGameGood() throws DataAccessException {
        var gameID = 12345;
        var whiteUsername = "username";
        var blackUsername = "username2";
        var gameName = "gameName";

        var gameData = new GameData(gameID, whiteUsername, blackUsername, gameName);
        gameDAO.insertGame(gameData);

        Assertions.assertEquals(gameDAO.findGame(gameID).getGameID(), gameData.getGameID());
    }

    @Test
    @Order(9)
    @DisplayName("InsertGame Test - Negative")
    public void insertGameBad() {
        var gameID = 12345;
        var whiteUsername = "username";
        var blackUsername = "username2";

        var gameData = new GameData(gameID, whiteUsername, blackUsername, null);

        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.insertGame(gameData));
    }

    @Test
    @Order(10)
    @DisplayName("FindGame Test - Positive")
    public void findGameGood() throws DataAccessException {
        var gameID = 12345;
        var whiteUsername = "username";
        var blackUsername = "username2";
        var gameName = "gameName";

        var gameData = new GameData(gameID, whiteUsername, blackUsername, gameName);
        gameDAO.insertGame(gameData);
        int result = gameDAO.findGame(gameID).getGameID();

        Assertions.assertEquals(result, gameID);
    }

    @Test
    @Order(11)
    @DisplayName("FindGame Test - Negative")
    public void findGameBad() throws DataAccessException {
        var gameID = 12345;
        var whiteUsername = "username";
        var blackUsername = "username2";
        var gameName = "gameName";

        var gameData = new GameData(gameID, whiteUsername, blackUsername, gameName);
        gameDAO.insertGame(gameData);

        Assertions.assertNull(gameDAO.findGame(123));
    }

    @Test
    @Order(12)
    @DisplayName("FindAll Test - Positive")
    public void findAllGood() throws DataAccessException, SQLException {

        var gameData = new GameData(12345, "username", "username2", "gameName");
        var gameData2 = new GameData(123456, "username3", "username4", "gameName2");

        gameDAO.insertGame(gameData);
        var result = gameDAO.findAll();

        Assertions.assertEquals(result.size(), 1);

        gameDAO.insertGame(gameData2);
        result = gameDAO.findAll();
        Assertions.assertEquals(result.size(), 2);
    }

    @Test
    @Order(13)
    @DisplayName("FindAll Test - Negative")
    public void findAllBad() throws DataAccessException, SQLException {

        var gameData = new GameData(12345, "username", "username2", "gameName");
        var gameData2 = new GameData(123456, "username3", "username4", "gameName2");

        gameDAO.insertGame(gameData);
        gameDAO.insertGame(gameData2);
        var result = gameDAO.findAll();

        Assertions.assertNotEquals(result.size(), 1);
    }

    @Test
    @Order(14)
    @DisplayName("ClaimSpot Test - Positive")
    public void claimSpotGood() throws DataAccessException {
        String username = "usernamewhite";
        String color = "white";
        int gameID = 123;

        var gameData = new GameData(123, null, null, "gamename");
        gameDAO.insertGame(gameData);

        gameDAO.claimSpot(username, color, gameID);

        var result = gameDAO.findGame(123).getWhiteUsername();

        Assertions.assertEquals(result, username);
    }

    @Test
    @Order(14)
    @DisplayName("ClaimSpot Test - Negative")
    public void claimSpotBad() throws DataAccessException {
        String username = "usernamewhite";
        String color = "white";
        int gameID = 123;

        var gameData = new GameData(123, null, null, "gamename");
        gameDAO.insertGame(gameData);
        Assertions.assertNotNull(gameDAO.findGame(gameID));

        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.claimSpot(username, color, -1234));
    }

    @Test
    @Order(15)
    @DisplayName("GameDAO ClearTokens Test - Positive")
    public void gameClearTokens() throws DataAccessException {
        var gameID = 12345;
        var whiteUsername = "username";
        var blackUsername = "username2";
        var gameName = "gameName";

        var gameData = new GameData(gameID, whiteUsername, blackUsername, gameName);
        gameDAO.insertGame(gameData);
        int result = gameDAO.findGame(gameID).getGameID();

        Assertions.assertEquals(result, gameID);

        gameDAO.clearTokens();

        Assertions.assertNull(gameDAO.findGame(12345));
    }

    /* Begin UserDAO Tests */

    @Test
    @Order(16)
    @DisplayName("InsertUser Test - Positive")
    public void insertUserGood() throws DataAccessException, SQLException {
        String username = "username";
        String password = "password";
        String email = "email";

        var userInfo = new UserData(username, password, email);

        userDAO.insertUser(userInfo);

        var result = userDAO.findUser(username).getUserName();

        Assertions.assertEquals(result, username);
    }

    @Test
    @Order(17)
    @DisplayName("InsertUser Test - Negative")
    public void insertUserBad() throws DataAccessException, SQLException {
        String username = "username";
        String password = "password";

        var userInfo = new UserData(username, password, null);

        Assertions.assertThrows(DataAccessException.class, () -> userDAO.insertUser(userInfo));
    }

    @Test
    @Order(18)
    @DisplayName("VerifyUser Test - Positive")
    public void verifyUserGood() throws DataAccessException, SQLException {
        String username = "username";
        String password = "password";
        String email = "email";

        var userInfo = new UserData(username, password, email);

        userDAO.insertUser(userInfo);

        var result = userDAO.findUser(username).getUserName();

        Assertions.assertEquals(result, username);

        Assertions.assertEquals(userDAO.verifyUser(userInfo), userInfo);
    }

    @Test
    @Order(19)
    @DisplayName("VerifyUser Test - Negative")
    public void verifyUserBad() throws DataAccessException, SQLException {
        String username = "username";
        String password = "password";
        String email = "email";

        var userInfo = new UserData(username, password, email);
        var wrongUserInfo = new UserData(username, "wrongpassword", email);

        userDAO.insertUser(userInfo);

        var result = userDAO.findUser(username).getUserName();

        Assertions.assertEquals(result, username);

        Assertions.assertNull(userDAO.verifyUser(wrongUserInfo));
    }

    @Test
    @Order(20)
    @DisplayName("FindUser Test - Positive")
    public void findUserGood() throws DataAccessException, SQLException {
        String username = "username1";
        String password = "password";
        String email = "email";

        var userInfo = new UserData(username, password, email);

        userDAO.insertUser(userInfo);

        var result = userDAO.findUser(username).getUserName();

        Assertions.assertEquals(result, username);
    }

    @Test
    @Order(21)
    @DisplayName("FindUser Test - Negative")
    public void findUserBad() throws DataAccessException, SQLException {
        String username = "username1";
        String password = "password";
        String email = "email";

        var userInfo = new UserData(username, password, email);

        userDAO.insertUser(userInfo);

        Assertions.assertNull(userDAO.findUser("username"));
    }

    @Test
    @Order(22)
    @DisplayName("UserDAO ClearTokens Test - Positive")
    public void userDAOClearTokens() throws DataAccessException, SQLException {
        String username = "username";
        String password = "password";
        String email = "email";

        var userInfo = new UserData(username, password, email);

        userDAO.insertUser(userInfo);

        var result = userDAO.findUser(username).getUserName();

        Assertions.assertEquals(result, username);

        userDAO.clearTokens();

        Assertions.assertNull(userDAO.findUser(username));
    }

}
