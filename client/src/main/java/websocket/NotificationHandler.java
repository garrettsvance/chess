package websocket;
import webSocketMessages.serverMessages.*;

import javax.management.Notification;

public interface NotificationHandler {
    void notify(Notification notification);
}
