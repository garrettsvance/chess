package server.Handler;

import SharedServices.LoginResult;
import service.ClearApplicationService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public record ClearApplicationHandler(ClearApplicationService clearApplicationService) implements Route {


    @Override
    public Object handle(Request sparkRequest, Response response) throws Exception {
        Gson gson = new Gson();
        try {
            var result = clearApplicationService.clear();
            if (result.message().equals("success")) {
                response.status(200);
            }
            return gson.toJson(result);
        } catch (Exception e) {
            LoginResult result = new LoginResult(null, null, "Error:" + e.getMessage());
            response.status(500);
            return gson.toJson(result);
        }
    }
}
