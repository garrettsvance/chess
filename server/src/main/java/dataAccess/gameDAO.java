package dataAccess;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class gameDAO {

    static Map<Integer, GameData> gameMap = new HashMap<>();

    public void insertGame(GameData game) {
        gameMap.put(game.getGameID(), game);
    }

    public GameData findGame(int gameID) {
        return gameMap.get(gameID);
    }

    public Collection<GameData> findAll() {
        return gameMap.values();
    }


    public void claimSpot(String userName, String color, Integer gameID) {
        if (color.equalsIgnoreCase("white")) {
            GameData game = findGame(gameID);
            game.setWhiteUsername(userName);
        } else if (color.equalsIgnoreCase("black")) {
            GameData game = findGame(gameID);
            game.setBlackUsername(userName);
        }
    }

    public void clearTokens() throws DataAccessException {
        gameMap.clear();
    }

}
