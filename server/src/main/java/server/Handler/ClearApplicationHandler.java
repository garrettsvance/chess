package server.Handler;
import service.LoginService;
import service.ClearApplicationService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearApplicationHandler implements Route {


    @Override
    public Object handle(Request sparkRequest, Response response) throws Exception { //TODO: should these all be dataAccessException?
        Gson gson = new Gson();
        try {
            ClearApplicationService.ClearApplicationResult result = ClearApplicationService.clear();
            if (result.message().equals("success")) {
                response.status(200);
            }
            return gson.toJson(result);
        } catch (Exception e) {
            LoginService.LoginResult result = new LoginService.LoginResult(null, null, "Error:" + e.getMessage());
            response.status(500);
            return gson.toJson(result);
        }
    }
}
