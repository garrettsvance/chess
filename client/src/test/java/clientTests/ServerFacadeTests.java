package clientTests;

import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import javax.xml.crypto.Data;
import java.util.NoSuchElementException;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        var serverUrl = "http://localhost:" + port;
        facade = new ServerFacade(serverUrl);
    }

    @BeforeEach
    void clearDataBases() throws DataAccessException {
        facade.clearDatabase();
    };


    @AfterAll
    static void stopServer() throws DataAccessException {
        facade.clearDatabase();
        server.stop();
    }


    @Test
    public void registerGood() throws DataAccessException {
        UserData user = new UserData("garrett", "vance", "email");
        var authData = facade.register(user);
        Assertions.assertNotNull(authData.getAuthToken());
    }

    @Test
    public void registerBad() {
        UserData user = new UserData(null, "vance", "email");
        Assertions.assertThrows(DataAccessException.class, () -> {facade.register(user);});
    }

    @Test
    public void loginGood() throws DataAccessException {
        UserData user = new UserData("garrett", "vance", "email");
        facade.register(user);
        var authData = facade.login(user);
        Assertions.assertNotNull(authData.getAuthToken());
    }

    @Test
    public void loginBad() throws DataAccessException {
        UserData user = new UserData("garrett", "vance", "email");
        facade.register(user);
        UserData badUser = new UserData("garrett", "van", "email");
        Assertions.assertThrows(DataAccessException.class, () -> {facade.login(badUser);});
    }

    @Test
    public void logoutGood() throws DataAccessException {
        UserData user = new UserData("garrett", "vance", "email");
        var authData = facade.register(user);
        facade.logout(authData);
        Assertions.assertThrows(DataAccessException.class, () -> {facade.createGame(authData, "game1");});
    }

    @Test
    public void logoutBad() {
        Assertions.assertThrows(DataAccessException.class, () -> {facade.logout(null);});
    }

    @Test
    public void listGamesGood() throws DataAccessException {
        UserData user = new UserData("garrett", "vance", "email");
        facade.register(user);
        var authData = facade.login(user);
        facade.createGame(authData, "game1");
        facade.createGame(authData, "game2");
        Assertions.assertNotNull(facade.listGames(authData));
    }

    @Test
    public void listGamesBad() {
        Assertions.assertThrows(DataAccessException.class, () -> {facade.listGames(null);});
    }

    @Test
    public void createGameGood() throws DataAccessException {
        UserData user = new UserData("garrett", "vance", "email");
        facade.register(user);
        var authData = facade.login(user);
        facade.createGame(authData, "game1");
        Assertions.assertNotNull(facade.listGames(authData));
    }

    @Test
    public void createGameBad() throws DataAccessException {
        UserData user = new UserData("garrett", "vance", "email");
        facade.register(user);
        var authData = facade.login(user);
        Assertions.assertThrows(DataAccessException.class, () -> {facade.createGame(authData, null);});
    }

    @Test
    public void joinGameGood() throws DataAccessException {
        UserData user = new UserData("garrett", "vance", "email");
        facade.register(user);
        var authData = facade.login(user);
        var gameID = (facade.createGame(authData, "game1")).getGameID();
        facade.joinGame(authData, "white", gameID);
        GameData joinedGame = facade.listGames(authData).get(0);
        Assertions.assertEquals("garrett", joinedGame.getWhiteUsername());
    }

    @Test
    public void joinGameBad() throws DataAccessException {
        UserData user = new UserData("garrett", "vance", "email");
        facade.register(user);
        var authData = facade.login(user);
        var gameID = (facade.createGame(authData, "game1")).getGameID();
        Assertions.assertThrows(DataAccessException.class, () -> {facade.joinGame(authData, "white", gameID + 3);});
    }



}
