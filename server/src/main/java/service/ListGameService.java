package service;
import model.GameData;
import dataAccess.AuthTokenDAO;
import dataAccess.GameDAO;

import java.util.Collection;

public class ListGameService {

    public record ListGamesResult(Collection<GameData> games, String message) {}
    static AuthTokenDAO authDAO = new AuthTokenDAO();
    static GameDAO games = new GameDAO();

    public static ListGamesResult listGames(String authToken) {

        if (authDAO.findToken(authToken) == null) {
            return new ListGamesResult(null,"Error: unauthorized");
        } else {
            Collection<GameData> listOfGames = games.findAll();
            return new ListGamesResult(listOfGames, "success");
        }
    }

}