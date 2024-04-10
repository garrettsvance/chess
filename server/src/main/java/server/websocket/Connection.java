package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

public record Connection (int gameID, Session session) {}

