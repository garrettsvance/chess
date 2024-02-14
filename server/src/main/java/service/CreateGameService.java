package service;
import model.GameData;
import dataAccess.authTokenDAO;
import dataAccess.gameDAO;

import java.util.UUID;

public class CreateGameService {

    record CreateGameRequest(String gameName) {}
    record CreateGameResult(Integer gameID, String message) {}
    static authTokenDAO authDAO = new authTokenDAO();
    static gameDAO gameMap = new gameDAO();

    public static CreateGameResult createGame(CreateGameRequest request, String authToken) {
        String gameName = request.gameName;
        int gameID = Math.abs(UUID.randomUUID().hashCode());
        if (authDAO.findToken(authToken) == null) {
            return new CreateGameResult( null, "Error: unauthorized");
        } else if (gameName == null){
            return new CreateGameResult(null, "Error: bad request");
        } else {
            GameData game = new GameData(gameID, null, null, gameName);
            gameMap.insertGame(game);
            return new CreateGameResult(gameID, "success");
        }
    }

}