package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public interface GameDAO {

    void insertGame(GameData game) throws DataAccessException;

    GameData findGame(int gameID) throws DataAccessException;

    Collection<GameData> findAll() throws DataAccessException, SQLException;

    void claimSpot(String userName, String color, Integer gameID) throws DataAccessException;

    void updateGame(ChessGame game, int gameID) throws DataAccessException, SQLException;

    void clearTokens() throws DataAccessException;

}
