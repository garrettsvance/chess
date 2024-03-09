package service;
import dataAccess.*;
import model.AuthData;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public class ListGameService {

    private AuthTokenDAO authDAO;
    private GameDAO gameDAO;

    public ListGameService(AuthTokenDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public record ListGamesResult(Collection<GameData> games, String message) {}
    //static MemoryAuthTokenDAO authDAO = new MemoryAuthTokenDAO();
    //static MemoryGameDAO games = new MemoryGameDAO();

    public ListGamesResult listGames(String authToken) throws SQLException, DataAccessException {

        if (authDAO.findToken(authToken) == null) {
            return new ListGamesResult(null,"Error: unauthorized");
        } else {
            Collection<GameData> listOfGames = gameDAO.findAll();
            return new ListGamesResult(listOfGames, "success");
        }
    }

}