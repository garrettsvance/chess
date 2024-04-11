package server;

import dataAccess.*;
import server.websocket.WebSocketHandler;
import service.*;
import spark.*;

public class Server {

    private WebSocketHandler webSocketHandler = new WebSocketHandler();

    public int run(int desiredPort) {

        AuthTokenDAO authDAO;
        GameDAO gameDAO;
        UserDAO userDAO;

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");


        if (false) {
            userDAO = new MemoryUserDAO();
            authDAO = new MemoryAuthTokenDAO();
            gameDAO = new MemoryGameDAO();
        } else {
            try {
                DatabaseManager.createDatabase();
                userDAO = new SQLUserDAO();
                authDAO = new SQLAuthTokenDAO();
                gameDAO = new SQLGameDAO();
            } catch (Exception e) {
                System.out.println("Runtime Exception");
                throw new RuntimeException(e);
            }
        }

        LoginService loginService = new LoginService(userDAO, authDAO);
        RegisterService registerService = new RegisterService(userDAO, authDAO);
        ClearApplicationService clearApplicationService = new ClearApplicationService(userDAO, authDAO, gameDAO);
        LogoutService logoutService = new LogoutService(authDAO);
        ListGameService listGameService = new ListGameService(authDAO, gameDAO);
        CreateGameService createGameService = new CreateGameService(authDAO, gameDAO);
        JoinGameService joinGameService = new JoinGameService(authDAO, gameDAO);

        Spark.webSocket("/connect", webSocketHandler);
        Spark.post("/session", new server.Handler.LoginHandler(loginService));
        Spark.delete("/db", new server.Handler.ClearApplicationHandler(clearApplicationService));
        Spark.post("/user", new server.Handler.RegisterHandler(registerService));
        Spark.delete("/session", new server.Handler.LogoutHandler(logoutService));
        Spark.get("/game", new server.Handler.ListGamesHandler(listGameService));
        Spark.post("/game", new server.Handler.CreateGameHandler(createGameService));
        Spark.put("/game", new server.Handler.JoinGameHandler(joinGameService));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}
