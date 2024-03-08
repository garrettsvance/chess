package server;
import com.google.gson.Gson;
import dataAccess.*;
import service.*;
import spark.*;

public class Server {

    private final AuthTokenDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public Server() {
        try{
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthTokenDAO();
            gameDAO = new SQLGameDAO();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


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

//    private Object login(Request request, Response response) throws Exception {
//        return LoginHandler.handle(request, response);
//    }
//
//    private Object clearApplication(Request request, Response response) throws Exception {
//        return ClearApplicationHandler.handle(request, response);
//    }
//
//    private Object register(Request request, Response response) throws Exception {
//        return RegisterHandler.handle(request, response);
//    }
//    private Object logout(Request request, Response response) throws Exception {
//        return LogoutHandler.handle(request, response);
//    }

    public Object handleLogin(Request sparkRequest, Response response) throws Exception {
        Gson gson = new Gson();
        try {
            LoginService.LoginRequest request = gson.fromJson(sparkRequest.body(), LoginService.LoginRequest.class);
            LoginService.LoginResult result = LoginService.login(request);
            switch(result.message()) {
                case "success" -> response.status(200);
                case "Error: unauthorized" -> response.status(401);
            }
            return gson.toJson(result);
        } catch(Exception e) {
            LoginService.LoginResult result = new LoginService.LoginResult(null, null, "Error:" + e.getMessage());
            response.status(500);
            return gson.toJson(result);
        }
    }


}
