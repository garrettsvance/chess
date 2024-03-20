package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class MenuUI {

    private final PrintStream out;

    public MenuUI() {this.out = new PrintStream(System.out, true, StandardCharsets.UTF_8);}

    public void preLogin(PrintStream out) {
        out.println("Enter Menu Selection: ");
        out.println("1. Help");
        out.println("2. Quit");
        out.println("3. Login");
        out.println("4. Register");
    }

    public void postLogin(PrintStream out) {
        out.println("Enter Menu Selection: ");
        out.println("1. Help");
        out.println("2. Logout");
        out.println("3. Create Game");
        out.println("4. List Games");
        out.println("5. Join Game");
        out.println("6. Join Game as Observer");
    }

    public void preLoginHelp(PrintStream out) {
        out.println("Help - show command structure");
        out.println("Quit - exit program");
        out.println("Login - <USERNAME> <PASSWORD> - sign into account");
        out.println("Register - <USERNAME> <PASSWORD> <EMAIL> - create an account");
    }

    public void postLoginHelp(PrintStream out) {
        out.println("Help - show command structure");
        out.println("Logout - sign out of account");
        out.println("Create Game - <NAME> - start a new game");
        out.println("List Games - show all created games");
        out.println("Join Game - <GAMEID> [WHITE|BLACK|<empty>] - join a created game");
        out.println("Join Observer - <GAMEID> - watch a created game");
    }

}
