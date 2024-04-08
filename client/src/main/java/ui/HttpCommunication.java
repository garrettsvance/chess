package ui;

import chess.ChessGame;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;
import websocket.WebSocketFacade;
import ui.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;


public class HttpCommunication {
// for phase 6, this becomes "http communication/communicator"
    private final ServerFacade server;
    private AuthData authData;
    MenuUI menuUI = new MenuUI();
    ChessBoardUI chessBoardUI = new ChessBoardUI();
    GamePlayUI gamePlayUI = new GamePlayUI();
    Scanner scanner = new Scanner(System.in);
    ChessGame game = null;

    int menuNum;

    public HttpCommunication(String serverURL) {
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

    private void preLoginChoice(int menuNum, PrintStream out) {
        switch (menuNum) {
            case 1 -> menuUI.preLoginHelp(out);
            case 2 -> quit(out);
            case 3 -> login(out);
            case 4 -> register(out);
            default -> out.println("Invalid Number");
        }
    }

    private void postLoginChoice(int menuNum, PrintStream out) {
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

    private void gamePlayChoice(int menuNum, PrintStream out) {
        switch (menuNum) {
            case 1 -> gamePlayUI.gamePlayHelp(out);
            case 2 -> redrawBoard(out);
            case 3 -> leave(out);
            case 4 -> makeMove(out);
            case 5 -> resign(out);
            case 6 -> highlightLegalMoves(out);
            default -> out.println("Invalid Number");
        }
    }

    public void quit(PrintStream out) {
        out.println("Quit");
        game = null;
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
                out.println("Success!");
                menuUI.postLogin(out);
                menuNum = scanner.nextInt();
                postLoginChoice(menuNum, out);
            } while (menuNum != 2);
            out.println();
            out.println();
            run();
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

        if (Objects.equals(username, "") || Objects.equals(password, "") || Objects.equals(email, "")) {
            out.println("All parameters must be fulfilled to register user.");
            out.println();
            out.println();
            run();
        }
        UserData user = new UserData(username, password, email);

        try {
            AuthData response = server.register(user);
            String authTokenString = response.getAuthToken();
            authData = new AuthData(username, authTokenString);
            do {
                menuUI.postLogin(out);
                menuNum = scanner.nextInt();
                postLoginChoice(menuNum, out);
            } while (menuNum != 2);
            run();
        } catch (DataAccessException e) {
            out.println("Registration Failed: " + e.getMessage());
        }

    }

    public void logout(PrintStream out) {
        out.println("Logout");
        try {
            game = null;
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
                out.println("Game Name: " + game.getGameName() + ", Game ID: " + game.getGameID() +
                        ", White Username: " + game.getWhiteUsername() + ", Black Username: " + game.getBlackUsername()
                );
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
        scanner.nextLine();
        String playerColor = scanner.nextLine();
        try {
            ChessGame response = server.joinGame(authData, playerColor, gameID);
            game = response;
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
            game = response;
            chessBoardUI.printBoard(response);
        } catch (DataAccessException e) {
            out.println("Join Game as Observer Failed: " + e.getMessage());
        }
    }

    public void redrawBoard(PrintStream out) {
        out.println("Redraw Chess Board");
        chessBoardUI.printBoard(game);
    }

    public void leave(PrintStream out) {

    }


    private class ServerMessageHandler implements WebSocketFacade.ServerMessageListener {
        @Override
        public void onLoadGame(LoadGameMessage message) {
            System.out.println("Received LOAD_GAME message: " + message);

            webSocketGame = message.getGame();
        }

        @Override
        public void onNotification(NotificationMessage message) {
            System.out.println("Received NOTIFICATION message: " + message);

            System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
            System.out.println();
            System.out.println("Notification: " + message.getMessage());
            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
            System.out.println();
            System.out.print("Enter Menu Number: ");
            int menuChoice = scanner.nextInt();
            gamePlayChoice(menuChoice, System.out);

        }

        @Override
        public void onError(ErrorMessage message) {
            System.out.println("Received ERROR message: " + message);

            Scanner scanner = new Scanner(System.in);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
            System.out.println();
            System.out.println("ERROR DETECTED: " + message.getErrorMessage());
            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
            System.out.print("Enter Menu Number: ");
            int menuChoice = scanner.nextInt();
            gamePlayChoice(menuChoice, System.out);
        }
    }

}
