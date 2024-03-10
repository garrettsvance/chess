package service;
import dataAccess.*;
import model.AuthData;
import model.GameData;


public class JoinGameService {

    private final AuthTokenDAO authDAO;
    private final GameDAO gameDAO;

    public JoinGameService(AuthTokenDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }



    public record JoinGameRequest(String playerColor, Integer gameID, String authToken) {}
    public record JoinGameResult(String playerColor, Integer gameID, String message) {}

    public JoinGameResult joinGame(JoinGameRequest request, String authToken) throws DataAccessException {
        AuthData userToken = authDAO.findToken(authToken);
        int gameID = request.gameID();
        GameData game = gameDAO.findGame(gameID);

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
                gameDAO.claimSpot(userName, playerColor, gameID);
                return new JoinGameResult(null, null, "success");
            } else {
                return new JoinGameResult(null, null, "Error: already taken");
            }
        } else if (game.getBlackUsername() == null) {
            gameDAO.claimSpot(userName, playerColor, gameID);
            return new JoinGameResult(null, null, "success");
        } else {
            return new JoinGameResult(null, null, "Error: already taken");
        }
    }

}