package server.Handler;

import SharedServices.CreateGameRequest;
import SharedServices.CreateGameResult;
import service.CreateGameService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public record CreateGameHandler(CreateGameService createGameService) implements Route {

    @Override
    public Object handle(Request sparkRequest, Response response) throws Exception {
        Gson gson = new Gson();

        try {
            CreateGameRequest request = gson.fromJson(sparkRequest.body(), CreateGameRequest.class);
            String authToken = sparkRequest.headers("Authorization");
            CreateGameResult result = createGameService.createGame(request, authToken);
            switch (result.message()) {
                case "success" -> response.status(200);
                case "Error: unauthorized" -> response.status(401);
                case "Error: bad request" -> response.status(400);
            }
            return gson.toJson(result);
        } catch (Exception e) {
            CreateGameResult result = new CreateGameResult(null, "Error:" + e.getMessage(), null);
            response.status(500);
            return gson.toJson(result);
        }
    }
}