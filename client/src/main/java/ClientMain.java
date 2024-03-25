// this is the class that you run to start your server and such
// you'll create separate classes for your chessboard ui and menu ui

public class ClientMain {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        new ClientCommunication(serverUrl).run();
    }
}