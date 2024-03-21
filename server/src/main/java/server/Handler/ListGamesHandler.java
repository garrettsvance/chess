package server.Handler;

import SharedServices.ListGamesResult;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.ListGameService;

public record ListGamesHandler(ListGameService listGameService) implements Route {

    @Override
    public Object handle(Request sparkRequest, Response response) throws Exception {
        Gson gson = new Gson();
        try {
            String authToken = sparkRequest.headers("Authorization");
            var result = listGameService.listGames(authToken);
            switch (result.message()) {
                case "success" -> response.status(200);
                case "Error: unauthorized" -> response.status(401);
            }
            return gson.toJson(result);
        } catch (Exception e) {
            var result = new ListGamesResult(null, "Error: " + e.getMessage());
            response.status(500);
            return gson.toJson(result);
        }
    }
}
