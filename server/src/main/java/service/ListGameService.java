package service;
import model.GameData;
import dataAccess.authTokenDAO;
import dataAccess.gameDAO;

import java.util.Collection;

public class ListGameService {

    record ListGamesResult(Collection<GameData> games, String message) {}
    static authTokenDAO authDAO = new authTokenDAO();
    static gameDAO games = new gameDAO();

    public static ListGamesResult listGames(String authToken) {

        if (authDAO.findToken(authToken) == null) {
            return new ListGamesResult(null,"Error: unauthorized");
        } else {
            Collection<GameData> listOfGames = games.findAll();
            return new ListGamesResult(listOfGames, "success");
        }
    }

}