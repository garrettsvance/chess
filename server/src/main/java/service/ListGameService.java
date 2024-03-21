package service;
import SharedServices.ListGamesResult;
import dataAccess.*;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public class ListGameService {

    private final AuthTokenDAO authDAO;
    private final GameDAO gameDAO;

    public ListGameService(AuthTokenDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }


    public ListGamesResult listGames(String authToken) throws SQLException, DataAccessException {

        if (authDAO.findToken(authToken) == null) {
            return new ListGamesResult(null,"Error: unauthorized");
        } else {
            Collection<GameData> listOfGames = gameDAO.findAll();
            return new ListGamesResult(listOfGames, "success");
        }
    }

}