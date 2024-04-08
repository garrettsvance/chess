package webSocketMessages.serverMessages;

import javax.management.Notification;

public class NotificationMessage extends ServerMessage {

    private final String message;

    public NotificationMessage(String notificationMessage) {
        super(ServerMessageType.NOTIFICATION);
        this.message = notificationMessage;
    }

    public String getMessage() {
        return message;
    }

}
