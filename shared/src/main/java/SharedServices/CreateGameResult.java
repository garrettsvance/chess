package SharedServices;

import model.GameData;

public record CreateGameResult(GameData game, String message, Integer gameID) {}
