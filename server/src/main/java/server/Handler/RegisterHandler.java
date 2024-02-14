package server.Handler;

import service.RegisterService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    @Override
    public Object handle(Request sparkRequest, Response response) throws Exception {
        Gson gson = new Gson();
        try {
            RegisterService.RegisterRequest request = gson.fromJson(sparkRequest.body(), RegisterService.RegisterRequest.class);
            RegisterService.RegisterResult result = RegisterService.register(request);
            switch (result.message()) {
                case "success" -> response.status(200);
                case "Error: bad request" -> response.status(400);
                case "Error: already taken" -> response.status(403);
            }
            return gson.toJson(result);
        } catch(Exception e) {
            RegisterService.RegisterResult result = new RegisterService.RegisterResult(null, null, "Error:" + e.getMessage());
            response.status(500);
            return gson.toJson(result);
        }
    }
}
