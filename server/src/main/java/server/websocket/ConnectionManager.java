package server.websocket;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import webSocketMessages.serverMessages.NotificationMessage;

public class ConnectionManager {

    public final ConcurrentHashMap<String, Connection> sessions = new ConcurrentHashMap<>();

    public void add(String authToken, Connection session) {
        sessions.put(authToken, session);
    }

    private void broadcast(String message, int gameID, String excludedAuth) {
        for (var authToken : sessions.keySet()) {
            if (Objects.equals(sessions.get(authToken).gameID(), gameID) && !Objects.equals(authToken, excludedAuth)) {
                try {
                    var s = sessions.get(authToken).session();
                    if (s.isOpen()) {
                        s.getRemote().sendString(new Gson().toJson(new NotificationMessage(message)));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
