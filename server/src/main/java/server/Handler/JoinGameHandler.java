package server.Handler;

import service.JoinGameService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {

    public final JoinGameService joinGameService;

    public JoinGameHandler(JoinGameService joinGameService) {this.joinGameService = joinGameService;}

    @Override
    public Object handle(Request sparkRequest, Response response) throws Exception {
        Gson gson = new Gson();
        try {
            JoinGameService.JoinGameRequest request = gson.fromJson(sparkRequest.body(), JoinGameService.JoinGameRequest.class);
            String authToken = sparkRequest.headers("Authorization");
            JoinGameService.JoinGameResult result = joinGameService.joinGame(request, authToken);
            switch (result.message()) {
                case "success" -> response.status(200);
                case "Error: unauthorized" -> response.status(401);
                case "Error: bad request" -> response.status(400);
                case "Error: already taken" -> response.status(403);
            }
            return gson.toJson(result);
        } catch(Exception e) {
            JoinGameService.JoinGameResult result = new JoinGameService.JoinGameResult(null, null, "Error:" + e.getMessage());
            response.status(500);
            return gson.toJson(result);
        }
    }
}
