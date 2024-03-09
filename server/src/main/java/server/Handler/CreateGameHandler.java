package server.Handler;

import service.CreateGameService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {

    public final CreateGameService createGameService;

    public CreateGameHandler(CreateGameService createGameService) {this.createGameService = createGameService;}

    @Override
    public Object handle(Request sparkRequest, Response response) throws Exception {
        Gson gson = new Gson();

        try {
            CreateGameService.CreateGameRequest request = gson.fromJson(sparkRequest.body(), CreateGameService.CreateGameRequest.class);
            String authToken = sparkRequest.headers("Authorization");
            CreateGameService.CreateGameResult result = createGameService.createGame(request, authToken);
            switch (result.message()) {
                case "success" -> response.status(200);
                case "Error: unauthorized" -> response.status(401);
                case "Error: bad request" -> response.status(400);
            }
            return gson.toJson(result);
        } catch(Exception e) {
            CreateGameService.CreateGameResult result = new CreateGameService.CreateGameResult(null, "Error:" + e.getMessage());
            response.status(500);
            return gson.toJson(result);
        }
    }
}