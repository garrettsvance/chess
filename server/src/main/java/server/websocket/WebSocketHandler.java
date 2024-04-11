package server.websocket;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@WebSocket
public class WebSocketHandler {

    private AuthTokenDAO authDAO;
    private GameDAO gameDAO;
    private Session session;

    public final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();



    public WebSocketHandler() {
        try {
            authDAO = new SQLAuthTokenDAO();
            gameDAO = new SQLGameDAO();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        this.session = session;

        switch (action.getCommandType()) {
            case JOIN_OBSERVER -> joinObserver(new Gson().fromJson(message, JoinObserverCommand.class));
            case JOIN_PLAYER -> joinPlayer(new Gson().fromJson(message, JoinPlayerCommand.class));
            case LEAVE -> leave(new Gson().fromJson(message, LeaveCommand.class));
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCommand.class));
            case RESIGN -> resign(new Gson().fromJson(message, ResignCommand.class));
        }
    }


    private void joinObserver(JoinObserverCommand action) throws DataAccessException, IOException {
        int gameID = action.getGameID();
        String authToken = action.getAuthToken();
        String username = authDAO.findToken(authToken).getUserName();
        GameData gameData = gameDAO.findGame(gameID);


        if (username == null)  {
            sendErrorMessage("Unauthorized");
            return;
        }

        if (gameData == null) {
            sendErrorMessage("Game not found");
            return;
        }

        ChessGame gameObject = gameData.getGameObject();
        session.getRemote().sendString(new Gson().toJson(new LoadGameMessage(gameObject)));
        var ws = new WebSocketConnection(gameID, session);
        addConnection(authToken, ws);
        String message = username + " has joined the game as an observer.";
        broadcast(message, gameID, authToken);
    }


    private void joinPlayer(JoinPlayerCommand action) throws DataAccessException, IOException {
        int gameID = action.getGameID();
        String authToken = action.getAuthToken();
        String username = authDAO.findToken(authToken).getUserName();
        GameData gameData = gameDAO.findGame(gameID);
        String playerColorString;

        if (username == null) {
            sendErrorMessage("Error Joining Player, auth or playercolor null");
            return;
        }

        if (gameData == null) {
            sendErrorMessage("Game not found");
            return;
        }

        ChessGame.TeamColor playerColor = action.getPlayerColor();
        if (playerColor == null) {
            playerColorString = "observer.";
        } else {
            playerColorString = String.valueOf(playerColor);
        }


        ChessGame gameObject = gameData.getGameObject();
        session.getRemote().sendString(new Gson().toJson(new LoadGameMessage(gameObject)));
        var ws = new WebSocketConnection(gameID, session);
        addConnection(authToken, ws);
        String message = username + " joined the game as " + playerColorString;
        broadcast(message, gameID, authToken);
    }

    private void leave(LeaveCommand action) throws DataAccessException {
        int gameID = action.getGameID();
        String authToken = action.getAuthToken();
        String username = authDAO.findToken(authToken).getUserName();
        GameData gameData = gameDAO.findGame(gameID);

        if (username == null) {
            sendErrorMessage("Unauthorized");
            return;
        }

        if (gameData == null) {
            sendErrorMessage("Game not found");
            return;
        }

        userMap.get(gameID).remove(authToken);
        String message = username + " has left the game.";
        broadcast(message, gameID, authToken);
    }


    private void makeMove(MakeMoveCommand action) throws DataAccessException {
        int gameID = action.getGameID();
        String authToken = action.getAuthToken();
        String username = authDAO.findToken(authToken).getUserName();
        GameData gameData = gameDAO.findGame(gameID);
        String startPosition = String.valueOf(action.getMove().getStartPosition());
        String endPosition = String.valueOf(action.getMove().getEndPosition());



        if (username == null) {
            sendErrorMessage("Unauthorized");
            return;
        }

        if (gameData == null) {
            sendErrorMessage("Game not found");
            return;
        }

        ChessGame game = gameData.getGameObject();
        ChessGame.TeamColor turn = game.getTeamTurn();

        if (turn == null) {
            sendErrorMessage("Game Concluded");
            return;
        }

        if (game.isInCheck(turn)) {
            if (game.isInCheckmate(turn)) {
                game.setTeamTurn(null);
                sendErrorMessage(username + " is in checkmate.");
                return;
            } else {
                game.setTeamTurn(null);
                sendErrorMessage(username + " is in check.");
                return;
            }
        }

        ChessPiece.PieceType piece = game.getBoard().getPiece(action.getMove().getStartPosition()).getPieceType();
        ChessGame.TeamColor swapTurn = (turn == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        try {
            game.makeMove(action.getMove());
        } catch (InvalidMoveException e) {
            sendErrorMessage("Invalid Move");
        }


        game.setTeamTurn(swapTurn);
        loadBroadcast(gameID, game);
        String message = username + " moved their " + piece + " from " + startPosition + " to " + endPosition;
        broadcast(message, gameID, authToken);
    }

    private void resign(ResignCommand action) throws DataAccessException {
        int gameID = action.getGameID();
        String authToken = action.getAuthToken();
        String username = authDAO.findToken(authToken).getUserName();
        GameData gameData = gameDAO.findGame(gameID);

        if (username == null) {
            sendErrorMessage("Unauthorized");
            return;
        }

        if (gameData == null) {
            sendErrorMessage("Game not found");
            return;
        }

        gameData.getGameObject().setTeamTurn(null);
        String message = username + " has resigned from the game. Opponent wins!";
        broadcast(message, gameID, authToken);
    }

    private void sendMessage(String message, Session session) {
        try {
            session.getRemote().sendString(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendErrorMessage(String message) {
        ServerMessage error = new ErrorMessage(message);
        sendMessage(new Gson().toJson(error), session);
    }

    private void broadcast(String message, int gameID, String currentAuth) {
        List<String> tokens = userMap.get(gameID);
        for(String storedAuth : tokens) {
            if (!storedAuth.equals(currentAuth)) {
                NotificationMessage notificationMessage = new NotificationMessage(message);
                String notification = new Gson().toJson(notificationMessage);
                sendMessage(notification, sessions.get(storedAuth));
            }
        }
    }

    private void loadBroadcast(int gameID, ChessGame game){
        List<String> tokens = userMap.get(gameID);
        for (String storedAuth : tokens) {
            if (sessions.containsKey(storedAuth)) {
                Session session = sessions.get(storedAuth);
                LoadGameMessage loadGameMessage = new LoadGameMessage(game);
                sendMessage(new Gson().toJson(loadGameMessage), session);
            }
        }
    }


}
