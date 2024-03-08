package service;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import dataAccess.MemoryAuthTokenDAO;
import dataAccess.MemoryGameDAO;




public class JoinGameService {

    public record JoinGameRequest(String playerColor, Integer gameID, String authToken) {}
    public record JoinGameResult(String playerColor, Integer gameID, String message) {}

    static MemoryGameDAO gameMap = new MemoryGameDAO();
    static MemoryAuthTokenDAO authDAO = new MemoryAuthTokenDAO();

    public static JoinGameResult joinGame(JoinGameRequest request, String authToken) throws DataAccessException {
        AuthData userToken = authDAO.findToken(authToken);
        int gameID = request.gameID();
        GameData game = gameMap.findGame(gameID);

        if (authDAO.findToken(authToken) == null) {
            return new JoinGameResult(null, null, "Error: unauthorized");
        }
        String userName = userToken.getUserName();
        String playerColor = request.playerColor();
        if (game == null) {
            return new JoinGameResult(null, null, "Error: bad request");
        } else if (playerColor == null) {
            return new JoinGameResult(null, null, "success");
        } else if (playerColor.equalsIgnoreCase("white")) {
            if (game.getWhiteUsername() == null) {
                gameMap.claimSpot(userName, playerColor, gameID);
                return new JoinGameResult(null, null, "success");
            } else {
                return new JoinGameResult(null, null, "Error: already taken");
            }
        } else if (game.getBlackUsername() == null) {
            gameMap.claimSpot(userName, playerColor, gameID);
            return new JoinGameResult(null, null, "success");
        } else {
            return new JoinGameResult(null, null, "Error: already taken");
        }
    }

}