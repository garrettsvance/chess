package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataAccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


@WebSocket
public class WebSocketHandler {

    private AuthTokenDAO authDAO;
    private GameDAO gameDAO;
    private Session session;

    private ConcurrentHashMap<String, WebSocketConnection> sessions = new ConcurrentHashMap<>();



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

    private void joinHelper(int gameID, String authToken, String username, ChessGame.TeamColor playerColor) throws DataAccessException, IOException {
        GameData gameData = gameDAO.findGame(gameID);

        if (username == null) {
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

        String playerColorString = (playerColor == null) ? " as an observer." : " as " + playerColor;
        String message = username + " has joined the game" + playerColorString;
        broadcast(message, gameID, authToken);
    }

    private void addConnection(String authToken, WebSocketConnection ws) {
        sessions.put(authToken, ws);
    }


    private void joinObserver(JoinObserverCommand action) throws DataAccessException, IOException {
        int gameID = action.getGameID();
        String authToken = action.getAuthToken();
        String username = authDAO.findToken(authToken).getUserName();
        joinHelper(gameID, authToken, username, null);
    }



    private void joinPlayer(JoinPlayerCommand action) throws DataAccessException, IOException {
        int gameID = action.getGameID();
        String authToken = action.getAuthToken();
        String username = authDAO.findToken(authToken).getUserName();
        ChessGame.TeamColor playerColor = action.getPlayerColor();
        joinHelper(gameID, authToken, username, playerColor);
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

        sessions.remove(authToken);
        String message = username + " has left the game.";
        broadcast(message, gameID, authToken);
    }


    private void makeMove(MakeMoveCommand action) throws DataAccessException, SQLException {
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

        ChessPiece.PieceType piece = game.getBoard().getPiece(action.getMove().getStartPosition()).getPieceType();
        ChessGame.TeamColor swapTurn = (turn == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        try {
            game.makeMove(action.getMove());
        } catch (InvalidMoveException e) {
            sendErrorMessage("Invalid Move: " + e.getMessage());
        }


        game.setTeamTurn(swapTurn);
        gameDAO.updateGame(game, gameID);
        broadcastGame(gameID, "", game);
        String message = username + " moved their " + piece + " from " + startPosition + " to " + endPosition;
        broadcast(message, gameID, authToken);
    }

    private void resign(ResignCommand action) throws DataAccessException, SQLException {
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
        gameDAO.updateGame(gameData.getGameObject(), gameID);
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

    private void broadcast(String message, int gameID, String excludedAuth) {
        for (var authToken : sessions.keySet()) {
            if (Objects.equals(sessions.get(authToken).gameID(), gameID) && !Objects.equals(authToken, excludedAuth)) {
                try {
                    var s = sessions.get(authToken).session();
                    if (s.isOpen()) {
                        s.getRemote().sendString(new Gson().toJson(new NotificationMessage(message)));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void broadcastGame(int gameID, String excludedAuth, ChessGame game) {
        for (var authToken : sessions.keySet()) {
            if (Objects.equals(sessions.get(authToken).gameID(), gameID) && !Objects.equals(authToken, excludedAuth)) {
                try {
                    var s = sessions.get(authToken).session();
                    if (s.isOpen()) {
                        s.getRemote().sendString(new Gson().toJson(new LoadGameMessage(game)));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


}
