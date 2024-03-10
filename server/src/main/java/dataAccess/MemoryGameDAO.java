package dataAccess;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {

    static Map<Integer, GameData> gameMap = new HashMap<>();

    public void insertGame(GameData game) throws DataAccessException {
        gameMap.put(game.getGameID(), game);
    }

    public GameData findGame(int gameID) throws DataAccessException {
        return gameMap.get(gameID);
    }

    public Collection<GameData> findAll() throws DataAccessException {
        return gameMap.values();
    }

    public void claimSpot(String userName, String color, Integer gameID) throws DataAccessException {
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
