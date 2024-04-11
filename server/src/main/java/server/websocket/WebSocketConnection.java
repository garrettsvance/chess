package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

public record WebSocketConnection(int gameID, Session session) {}

