package Service;

import model.AuthData;
import model.GameData;
import Request.JoinGameRequest;
import Result.JoinGameResult;
import dataAccess.authTokenDAO;
import dataAccess.gameDAO;




public class JoinGameService {

    static gameDAO gameMap = new gameDAO();
    static authTokenDAO authDAO = new authTokenDAO();

    /**
     * Verifies that the specified game exists, and,
     * if a color is specified, adds the caller as the requested
     * color to the game. If no color is specified the user is
     * joined as an observer.
     *
     * @param request join game information
     * @return Either a successful join or error response
     */
    public static JoinGameResult joinGame(JoinGameRequest request, String authToken) {
        AuthData userToken = authDAO.findToken(authToken);
        int gameID = request.getGameID();
        GameData game = gameMap.findGame(gameID);


        if (authDAO.findToken(authToken) == null) {
            return new JoinGameResult(null, null, "Error: unauthorized");
        }
        String userName = userToken.getUserName();
        String playerColor = request.getPlayerColor();
        if (game == null) {
            return new JoinGameResult(null, null, "Error: bad request");
        } else if (playerColor == null){
            return new JoinGameResult(null, null, "success");
        } else if (playerColor.equalsIgnoreCase("white")) {
            if (game.getWhiteUserName() == null) {
                gameMap.claimSpot(userName, playerColor, gameID);
                return new JoinGameResult(null, null, "success");
            } else {
                return new JoinGameResult(null, null, "Error: already taken");
            }
        } else /*if (playerColor.equalsIgnoreCase("black"))*/ {
            if (game.getBlackUserName() == null) {
                gameMap.claimSpot(userName, playerColor, gameID);
                return new JoinGameResult(null, null, "success");
            } else {
                return new JoinGameResult(null, null, "Error: already taken");
            }
        }
    }
}