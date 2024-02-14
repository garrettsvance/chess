package server.Handler;

import service.LoginService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class  LoginHandler implements Route {
    @Override
    public Object handle(Request sparkRequest, Response response) throws Exception {
        Gson gson = new Gson();

        try {
            LoginService.LoginRequest request = gson.fromJson(sparkRequest.body(), LoginService.LoginRequest.class);
            LoginService.LoginResult result = LoginService.login(request);
            switch(result.message()) {
                case "success" -> response.status(200);
                case "Error: unauthorized" -> response.status(401);
            }
            return gson.toJson(result);
        } catch(Exception e) {
            LoginService.LoginResult result = new LoginService.LoginResult(null, null, "Error:" + e.getMessage());
            response.status(500);
            return gson.toJson(result);
        }
    }
}