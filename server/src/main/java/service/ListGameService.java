package service;
import dataAccess.DataAccessException;
import model.GameData;
import dataAccess.MemoryAuthTokenDAO;
import dataAccess.MemoryGameDAO;

import java.sql.SQLException;
import java.util.Collection;

public class ListGameService {

    public record ListGamesResult(Collection<GameData> games, String message) {}
    static MemoryAuthTokenDAO authDAO = new MemoryAuthTokenDAO();
    static MemoryGameDAO games = new MemoryGameDAO();

    public static ListGamesResult listGames(String authToken) throws SQLException, DataAccessException {

        if (authDAO.findToken(authToken) == null) {
            return new ListGamesResult(null,"Error: unauthorized");
        } else {
            Collection<GameData> listOfGames = games.findAll();
            return new ListGamesResult(listOfGames, "success");
        }
    }

}