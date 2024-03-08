import chess.*;
import server.Server;
import spark.Spark;

public class Main {
    public static void main(String[] args) {

        var server = new Server();
        try {
            server.run(8080);
        } catch (Exception e)  {
            System.out.println("Error in server start: " + e.getMessage());
        }
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Server: " + piece);
//
//        new Main().run();
//    }
//
//    public void run() {
//        Spark.port(8080);
//
//        Spark.staticFiles.location("web");
//
//        Spark.post("/session", new server.Handler.LoginHandler(loginService));
//        Spark.delete("/db", new server.Handler.ClearApplicationHandler());
//        Spark.post("/user", new server.Handler.RegisterHandler());
//        Spark.delete("/session", new server.Handler.LogoutHandler());
//        Spark.get("/game", new server.Handler.ListGamesHandler());
//        Spark.post("/game", new server.Handler.CreateGameHandler());
//        Spark.put("/game", new server.Handler.JoinGameHandler());
//
//        Spark.awaitInitialization();
//    }
//
//    public void stop() {
//        Spark.stop();
//        Spark.awaitStop();
    }

}