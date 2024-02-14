package server.Handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.LogoutService;

public class LogoutHandler implements Route {
    @Override
    public Object handle(Request sparkRequest, Response response) throws Exception {
        Gson gson = new Gson();
        try {
            String authToken = sparkRequest.headers("authorization");
            LogoutService.LogoutResult result = LogoutService.logout(authToken);
            switch(result.message()) {
                case "success" -> response.status(200);
                case "Error: unauthorized" -> response.status(401);
            }
            return gson.toJson(result);
        } catch(Exception e) {
            LogoutService.LogoutResult result = new LogoutService.LogoutResult("Error:" + e.getMessage());
            response.status(500);
            return gson.toJson(result);
        }
    }
}