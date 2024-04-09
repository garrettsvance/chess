package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;
import websocket.WebSocketFacade;
import ui.*;
import webSocketMessages.serverMessages.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;


public class HttpCommunication {
// for phase 6, this becomes "http communication/communicator"
    private final ServerFacade server;
    private AuthData authData;
    MenuUI menuUI = new MenuUI();
    ChessBoardUI chessBoardUI = new ChessBoardUI();
    Scanner scanner = new Scanner(System.in);
    ChessGame game = null;
    private WebSocketFacade ws;
    private GameData currentGame;
    private int currentGameID;
    private String alphas = "abcdefgh";
    private ChessGame.TeamColor playerColor;
    private final String serverURL;

    int menuNum;

    public HttpCommunication(String serverURL) {
        this.serverURL = serverURL;
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
            case 1 -> menuUI.gamePlayHelp(out);
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
        populateGameList();
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
        populateGameList();
        out.print("Game ID Number: ");
        currentGameID = scanner.nextInt();
        out.print("Player Color - [WHITE|BLACK|<empty>]: ");
        scanner.nextLine();
        String playerColor = scanner.nextLine();
        ChessGame.TeamColor teamColor = setTeamColor(playerColor);
        try {
            ChessGame response = server.joinGame(authData, playerColor, currentGameID) ;
            game = response;
            chessBoardUI.printBoard(response, false, null);

            ws = new WebSocketFacade(serverURL);
            ws.setMessageListener(new ServerMessageHandler());
            ws.joinPlayerSocket(currentGameID, teamColor, authData.getAuthToken());

            menuUI.gamePlay(out);
            do {
                menuNum = scanner.nextInt();
                gamePlayChoice(menuNum, out);
            } while (menuNum != 3 && menuNum != 5);
        } catch (Exception e) {
            out.println("Join Game Failed: " + e.getMessage());
        }
    }

    private ChessGame.TeamColor setTeamColor(String playerColor) {
        if (playerColor.equalsIgnoreCase("white")) {
            return ChessGame.TeamColor.WHITE;
        } else if (playerColor.equalsIgnoreCase("black")) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return null;
        }
    }

    public void joinObserver(PrintStream out) {
        out.println("Join as Observer");
        populateGameList();
        out.print("Game ID Number: ");
        int gameID = scanner.nextInt();

        try {
            ChessGame response = server.joinGame(authData, null, gameID);
            game = response;
            chessBoardUI.printBoard(response, false, null);

            ws = new WebSocketFacade(serverURL);
            ws.setMessageListener(new ServerMessageHandler());
            ws.joinPlayerSocket(currentGameID, playerColor, authData.getAuthToken());
            menuUI.gamePlay(out);
            do {
                menuNum = scanner.nextInt();
                gamePlayChoice(menuNum, out);
            } while (menuNum != 3 && menuNum != 5);
        } catch (Exception e) {
            out.println("Join Game as Observer Failed: " + e.getMessage());
        }
    }

    public void redrawBoard(PrintStream out) {
        out.println("Redraw Chess Board");
        chessBoardUI.printBoard(game, false, null);
    }

    public void leave(PrintStream out) {
        out.println("Leave");
        ws.leaveGameSocket(currentGameID, authData.getAuthToken());
    }

    public void makeMove(PrintStream out) {
        out.println("Make Move");
        out.print("Enter Coordinates of Piece to Move - [c3, d8, etc]: ");
        String coords = scanner.nextLine();
        if (!checkCoords(coords)) {
            out.println("Invalid Coordinates");
            out.println();
            makeMove(out);
        }
        ChessPosition startPosition = stringToPosition(coords);
        ChessPiece piece = chessBoardUI.getPiece(startPosition, game.getBoard());
        if (piece.getTeamColor() != playerColor) {
            out.println("You Can Only Move your Own Team");
            out.println();
            makeMove(out);
        }
        Collection<ChessMove> validMoves = game.validMoves(startPosition);
        out.print("Enter Coordinates of Destination Square - [c3, d8, etc]: ");
        String destCoords = scanner.nextLine();
        if (!checkCoords(destCoords)) {
            out.println("Invalid Coordinates");
            out.println();
            makeMove(out);
        }
        ChessPosition endPosition = stringToPosition(destCoords);
        ChessMove move = new ChessMove(startPosition, endPosition, null);

        boolean isValidMove = false;
        for (ChessMove validMove : validMoves) {
            if (validMove.getEndPosition().equals(endPosition)) {
                isValidMove = true;
                break;
            }
        }
        if (!isValidMove) {
            out.println("Not a Valid Move for the Selected Piece");
            makeMove(out);
        }

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && promotionMove(piece, move)) {
            out.print("Enter Promotion Piece Type - [Q|R|B|N]: ");
            String promotionString = scanner.nextLine();
            ChessPiece.PieceType promotionPiece = promotionPieceType(promotionString);
            move = new ChessMove(startPosition, endPosition, promotionPiece);
        }
        ws.makeMoveSocket(authData.getAuthToken(), currentGameID, move);
    }

    private ChessPiece.PieceType promotionPieceType(String promotionString) {
        return switch (promotionString.toUpperCase()) {
            case "Q" -> ChessPiece.PieceType.QUEEN;
            case "R" -> ChessPiece.PieceType.ROOK;
            case "B" -> ChessPiece.PieceType.BISHOP;
            case "N" -> ChessPiece.PieceType.KNIGHT;
            default -> throw new IllegalArgumentException("Invalid Promotion Character");
        };
    }

    private boolean promotionMove(ChessPiece piece, ChessMove move) {
        return ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && move.getEndPosition().getRow() == 8) ||
                (piece.getTeamColor() == ChessGame.TeamColor.BLACK && move.getEndPosition().getRow() == 1));
    }

    public void resign(PrintStream out) {
        out.println("Resign");
        ws.resignSocket(currentGameID, authData.getAuthToken());
    }

    public void highlightLegalMoves(PrintStream out) {
        out.println("Highlight Legal Moves");
        out.println("Enter Coordinates of Piece to Check - [c3, d8, etc]: ");
        String coords = scanner.nextLine();
        if (!checkCoords(coords)) {
            out.println("Invalid Coordinates");
            out.println();
            highlightLegalMoves(out);
        } else {
            ChessPosition startPosition = stringToPosition(coords);
            Collection<ChessMove> validMoves = game.validMoves(startPosition);
            chessBoardUI.printBoard(game, true, validMoves);
        }
    }

    public ChessPosition stringToPosition(String string) {
        char letterPosition = string.charAt(0);
        int col = alphas.indexOf(letterPosition) + 1;
        int row = Integer.parseInt(string.substring(1));
        return new ChessPosition(row, col);
    }

    public boolean checkCoords(String string) {
        char letter = string.charAt(0);
        int num = string.charAt(1) - '0';
        return alphas.indexOf(letter) != -1 && (num >= 1 && num <= 8);
    }

    private void populateGameList() {
        try {
            GameData[] gameList = server.listGames(authData).toArray(new GameData[0]);
        } catch (Exception e) {
            System.out.println("Error Populating GameList: " + e.getMessage());
        }
    }


    private class ServerMessageHandler implements WebSocketFacade.ServerMessageListener {
        @Override
        public void onLoadGame(LoadGameMessage message) {
            System.out.println("Received LOAD_GAME message: " + message);

            game = message.getGame();
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
            System.out.println("ERROR DETECTED: " + message.getMessage());
            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
            System.out.print("Enter Menu Number: ");
            int menuChoice = scanner.nextInt();
            gamePlayChoice(menuChoice, System.out);
        }
    }

}
