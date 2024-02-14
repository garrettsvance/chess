package server;

import org.eclipse.jetty.websocket.server.WebSocketServerConnection;
import spark.*;

import java.nio.file.Paths;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        //Spark.webSocket("/connect", WebSocketServerConnection.class);

        Spark.staticFiles.location("web");

        //var webDir = Paths.get(Server.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "web");
        //Spark.externalStaticFileLocation(webDir.toString());

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
