package server.Handler;

import SharedServices.JoinGameRequest;
import SharedServices.JoinGameResult;
import service.JoinGameService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public record JoinGameHandler(JoinGameService joinGameService) implements Route {

    @Override
    public Object handle(Request sparkRequest, Response response) throws Exception {
        Gson gson = new Gson();
        try {
            JoinGameRequest request = gson.fromJson(sparkRequest.body(), JoinGameRequest.class);
            String authToken = sparkRequest.headers("Authorization");
            JoinGameResult result = joinGameService.joinGame(request, authToken);
            switch (result.message()) {
                case "success" -> response.status(200);
                case "Error: unauthorized" -> response.status(401);
                case "Error: bad request" -> response.status(400);
                case "Error: already taken" -> response.status(403);
            }
            return gson.toJson(result);
        } catch (Exception e) {
            JoinGameResult result = new JoinGameResult(null, null, "Error:" + e.getMessage());
            response.status(500);
            return gson.toJson(result);
        }
    }
}
