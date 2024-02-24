package service;
import model.GameData;
import dataAccess.AuthTokenDAO;
import dataAccess.GameDAO;

import java.util.UUID;

public class CreateGameService {

    public record CreateGameRequest(String gameName) {}
    public record CreateGameResult(Integer gameID, String message) {}
    static AuthTokenDAO authDAO = new AuthTokenDAO();
    static GameDAO gameMap = new GameDAO();

    public static CreateGameResult createGame(CreateGameRequest request, String authToken) {
        String gameName = request.gameName();
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