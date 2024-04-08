package websocket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;


import javax.management.Notification; //TODO: check if correct import
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import chess.ChessGame;
import chess.ChessMove;

public class WebSocketFacade extends Endpoint {

    Session session;
    private ServerMessageListener messageListener;

    public WebSocketFacade(String url) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler((MessageHandler.Whole<String>) this::handleMessage);
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private void handleMessage(String message) {
        try {
            ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
            switch (serverMessage.getServerMessageType()) {
                case LOAD_GAME -> {
                    LoadGameMessage lgMessage = new Gson().fromJson(message, LoadGameMessage.class);
                    messageListener.onLoadGame(lgMessage);
                }
                case NOTIFICATION -> {
                    NotificationMessage nMessage = new Gson().fromJson(message, NotificationMessage.class);
                    messageListener.onNotification(nMessage);
                }
                case ERROR -> {
                    ErrorMessage eMessage = new Gson().fromJson(message, ErrorMessage.class);
                    messageListener.onError(eMessage);
                }
            }
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid JSON format: " + message);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public void setMessageListener(ServerMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    @ClientEndpoint
    public interface ServerMessageListener {
        void onLoadGame(LoadGameMessage message);
        void onNotification(NotificationMessage message);
        void onError(ErrorMessage message);
    }

    private void sendUserCommand(UserGameCommand command) {
        try {
            String jsonCommand = new Gson().toJson(command);
            session.getBasicRemote().sendText(jsonCommand);
        } catch (IOException ex) {
            System.err.println("Error Sending User Command: " + ex.getMessage());
        }
    }

    public void joinPlayerSocket(int gameID, ChessGame.TeamColor playerColor, String authToken) {
        JoinPlayerCommand command = new JoinPlayerCommand(authToken, gameID, playerColor);
        sendUserCommand(command);
    }

    public void observePlayerSocket(int gameID, String authToken) {
        JoinObserverCommand command = new JoinObserverCommand(authToken, gameID);
        sendUserCommand(command);
    }

    public void makeMoveSocket(String authToken, int gameID, ChessMove move) {
        MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, move);
        sendUserCommand(command);
    }

    public void leaveGameSocket(int gameID, String authToken) {
        LeaveCommand command = new LeaveCommand(authToken, gameID);
        sendUserCommand(command);
    }

    public void resignSocket(int gameID, String authToken) {
        ResignCommand command = new ResignCommand(authToken, gameID);
        sendUserCommand(command);
    }



}
