package service;
import dataAccess.*;
import model.GameData;

import java.util.UUID;

public class CreateGameService {

    private final AuthTokenDAO authDAO;
    private final GameDAO gameDAO;

    public CreateGameService(AuthTokenDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public record CreateGameRequest(String gameName, String whiteUsername, String blackUsername) {}
    public record CreateGameResult(GameData game, String message, Integer gameID) {}

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        String gameName = request.gameName();

        int gameID = Math.abs(UUID.randomUUID().hashCode());
        if (authDAO.findToken(authToken) == null) {
            return new CreateGameResult( null, "Error: unauthorized", null);
        } else if (gameName == null){
            return new CreateGameResult(null, "Error: bad request", null);
        } else {
            GameData game = new GameData(gameID, request.whiteUsername(), request.blackUsername(), gameName);
            gameDAO.insertGame(game);
            return new CreateGameResult(game, "success", game.getGameID());
        }
    }

}