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
    }

}