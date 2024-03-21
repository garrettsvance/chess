package server.Handler;

import SharedServices.LoginRequest;
import SharedServices.LoginResult;
import service.LoginService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public record LoginHandler(LoginService loginService) implements Route {

    @Override
    public Object handle(Request sparkRequest, Response response) throws Exception {
        Gson gson = new Gson();

        try {
            var request = gson.fromJson(sparkRequest.body(), LoginRequest.class);
            var result = loginService.login(request);
            switch (result.message()) {
                case "success" -> response.status(200);
                case "Error: unauthorized" -> response.status(401);
            }
            return gson.toJson(result);
        } catch (Exception e) {
            LoginResult result = new LoginResult(null, null, "Error:" + e.getMessage());
            response.status(500);
            return gson.toJson(result);
        }
    }
}