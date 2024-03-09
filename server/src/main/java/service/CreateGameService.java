package service;
import dataAccess.*;
import model.GameData;

import java.util.UUID;

public class CreateGameService {

    private AuthTokenDAO authDAO;
    private GameDAO gameDAO;

    public CreateGameService(AuthTokenDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public record CreateGameRequest(String gameName) {}
    public record CreateGameResult(Integer gameID, String message) {}
    //static MemoryAuthTokenDAO authDAO = new MemoryAuthTokenDAO();
    //static MemoryGameDAO gameMap = new MemoryGameDAO();

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        String gameName = request.gameName();
        int gameID = Math.abs(UUID.randomUUID().hashCode());
        if (authDAO.findToken(authToken) == null) {
            return new CreateGameResult( null, "Error: unauthorized");
        } else if (gameName == null){
            return new CreateGameResult(null, "Error: bad request");
        } else {
            GameData game = new GameData(gameID, null, null, gameName);
            gameDAO.insertGame(game);
            return new CreateGameResult(gameID, "success");
        }
    }

}