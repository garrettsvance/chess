package server.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import org.eclipse.jetty.websocket.api.Session;
import org.springframework.security.core.parameters.P;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@WebSocket
public class WebSocketHandler {

    private AuthTokenDAO authDAO;
    private GameDAO gameDAO;
    private Session session;

    public final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, List<String>> userMap = new ConcurrentHashMap<>();



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

    private void joinObserver(JoinObserverCommand action) throws DataAccessException {
        int gameID = action.getGameID();
        String authToken = action.getAuthToken();
        String user = authDAO.findToken(authToken).getUserName();
        GameData gameData = gameDAO.findGame(gameID);

        if (user == null) {
            sendErrorMessage("Unauthorized");
            return;
        }

        if (gameData == null) {
            sendErrorMessage("Game not found");
            return;
        }

        sessions.put(authToken, session);
        userMap.computeIfAbsent(gameID, k -> new CopyOnWriteArrayList<>()).add(authToken);
        LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.getGameObject());

    }

    private void joinPlayer(JoinPlayerCommand action) throws DataAccessException {

    }

    private void leave(LeaveCommand action) throws DataAccessException {

    }

    private void leaveHelper(int gameID, String authToken) {

    }

    private void makeMove(MakeMoveCommand action) throws DataAccessException {

    }

    private void resign(ResignCommand action) throws DataAccessException {

    }

    private void notifyCurrentClient(String message) {
        NotificationMessage notificationMessage = new NotificationMessage(message);
        sendMessage(new Gson().toJson(notificationMessage), session);
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


}
