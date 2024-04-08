package webSocketMessages.serverMessages;

public class ErrorMessage extends ServerMessage {

    private final String message;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.message = errorMessage;
    }

    public String getMessage() {
        return message;
    }

}
