import chess.ChessGame;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.DisplayNameGenerator;
import ui.ChessBoardUI;
import ui.EscapeSequences;
import ui.MenuUI;

import javax.xml.crypto.Data;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientCommunication {
// for phase 6, this becomes "http communication/communicator"
    private final ServerFacade server;
    private AuthData authData;
    MenuUI menuUI = new MenuUI();
    ChessBoardUI chessBoardUI = new ChessBoardUI();
    Scanner scanner = new Scanner(System.in);

    int menuNum;

    public ClientCommunication(String serverURL) {
        server = new ServerFacade(serverURL);
    }

    public void run() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(EscapeSequences.ERASE_SCREEN);
        Scanner scanner = new Scanner(System.in);

        do {
            menuUI.preLogin(out);
            menuNum = scanner.nextInt();
            preLoginChoice(menuNum, out);
        } while (menuNum != 2);
    }

    public void preLoginChoice(int menuNum, PrintStream out) {
        switch (menuNum) {
            case 1 -> menuUI.preLoginHelp(out);
            case 2 -> quit(out);
            case 3 -> login(out);
            case 4 -> register(out);
            default -> out.println("Invalid Number");
        }
    }

    public void postLoginChoice(int menuNum, PrintStream out) {
        switch (menuNum) {
            case 1 -> menuUI.postLoginHelp(out);
            case 2 -> logout(out);
            case 3 -> createGame(out);
            case 4 -> listGames(out);
            case 5 -> joinGame(out);
            case 6 -> joinObserver(out);
            default -> out.println("Invalid Number");
        }
    }

    public void quit(PrintStream out) {
        out.println("Quit");
        authData = null;
    }

    public void login(PrintStream out) {
        out.println("Login");
        out.println("Username: ");
        String username = scanner.nextLine();
        out.println("Password: ");
        String password = scanner.nextLine();
        out.println();

        UserData user = new UserData(username, password, null);
        try {
            AuthData response = server.login(user);
            String authTokenString = response.getAuthToken();
            authData = new AuthData(authTokenString, username);
            do {
                menuUI.postLogin(out);
                menuNum = scanner.nextInt();
                postLoginChoice(menuNum, out);
            } while (menuNum != 2);
        } catch (DataAccessException e) {
            out.println("Login Failed: " + e.getMessage());
        }
    }

    public void register(PrintStream out) {
        out.println("Register");
        out.print("New Username: ");
        String username = scanner.nextLine();
        out.print("New Password: ");
        String password = scanner.nextLine();
        out.print("New Email: ");
        String email = scanner.nextLine();

        UserData user = new UserData(username, password, email);

        try {
            AuthData response = server.register(user); //TODO: make sure to interpret response properly
            String authTokenString = response.getAuthToken();
            authData = new AuthData(username, authTokenString);
            do {
                menuUI.postLogin(out);
                menuNum = scanner.nextInt();
                postLoginChoice(menuNum, out);
            } while (menuNum != 2);
        } catch (DataAccessException e) {
            out.println("Registration Failed: " + e.getMessage());
        }

    }

    public void logout(PrintStream out) {
        out.println("Logout");
        try {
            server.logout(authData);
        } catch (DataAccessException e) {
            out.println("Logout Failed: " + e.getMessage());
        }
    }

    public void createGame(PrintStream out) {
        String gameName;
        out.println("Create Game");
        out.println("Game Name: ");
        scanner.nextLine();
        gameName = scanner.nextLine();
        try {
            server.createGame(authData, gameName);
        } catch (DataAccessException e) {
            out.println("Create Game Failed: " + e.getMessage());
        }
    }

    public void listGames(PrintStream out) {
        out.println("List Games");
        try {
            var response = server.listGames(authData);
            for (GameData game : response) {
                out.println("Game Name: " + game.getGameName() + ", Game ID: " + game.getGameID());
            }
        } catch (DataAccessException e) {
            out.println("List Games Failed: " + e.getMessage());
        }
    }

    public void joinGame(PrintStream out) {
        out.println("Join Game");
        out.print("Game ID Number: ");
        int gameID = scanner.nextInt();
        out.print("Player Color - [WHITE|BLACK|<empty>]: ");
        String playerColor = scanner.nextLine();
        try {
            ChessGame response = server.joinGame(authData, playerColor, gameID);
            chessBoardUI.printBoard(response);
        } catch (DataAccessException e) {
            out.println("Join Game Failed: " + e.getMessage());
        }
    }

    public void joinObserver(PrintStream out) {
        out.println("Join as Observer");
        out.print("Game ID Number: ");
        int gameID = scanner.nextInt();

        try {
            ChessGame response = server.joinGame(authData, null, gameID);
            chessBoardUI.printBoard(response);
        } catch (DataAccessException e) {
            out.println("Join Game as Observer Failed: " + e.getMessage());
        }
    }




}
