package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/session", new server.Handler.LoginHandler());
        Spark.delete("/db", new server.Handler.ClearApplicationHandler());
        Spark.post("/user", new server.Handler.RegisterHandler());
        Spark.delete("/session", new server.Handler.LogoutHandler());
        Spark.get("/game", new server.Handler.ListGamesHandler());
        Spark.post("/game", new server.Handler.CreateGameHandler());
        Spark.put("/game", new server.Handler.JoinGameHandler());

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
