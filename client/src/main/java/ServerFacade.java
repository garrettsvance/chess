import SharedServices.JoinGameRequest;
import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.CreateGameService;
import service.JoinGameService;
import service.LoginService;
import service.RegisterService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    //each of these 7 methods should be one to two lines of code, and should call the actual coded methods in client communication
    private String serverURL;

    public ServerFacade(String url) {
        serverURL = url;
    }


    public AuthData login(UserData user) throws DataAccessException { // TODO: should this be a handler type?
        var request = new LoginService.LoginRequest(user.getUserName(), user.getPassword(), user.getEmail());
        return this.makeRequest("POST", "/session", request, AuthData.class, null);
    }

    public AuthData register(UserData user) throws DataAccessException {
        var request = new RegisterService.RegisterRequest(user.getUserName(), user.getPassword(), user.getEmail());
        return this.makeRequest("POST", "/user", request, AuthData.class, null);
    }

    public void clearDatabase() throws DataAccessException {
        this.makeRequest("DELETE", "/db", null, null, null);
    }

    public void logout(AuthData authToken) throws DataAccessException {

        this.makeRequest("DELETE", "/session", null, null, authToken);
    }

    public GameData listGames(AuthData authToken) throws DataAccessException {
        record ListGamesResponse(GameData games) {}
        ListGamesResponse response = this.makeRequest("GET", "/game", null, ListGamesResponse.class, authToken);
        return response.games();
    }

    public void createGame(AuthData authToken, String gameName) throws DataAccessException {
        var request = new CreateGameService.CreateGameRequest(gameName, null, null);
        this.makeRequest("POST", "/game", request, GameData.class, authToken);
    }

    public ChessGame joinGame(AuthData authToken, String playerColor, Integer gameID) throws DataAccessException {
        String auth = authToken.getAuthToken();
        var request = new JoinGameRequest(playerColor, gameID, auth);
        return makeRequest("PUT", "/game", request, ChessGame.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, AuthData auth) throws DataAccessException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            if (auth != null) {
                http.setRequestProperty("authorization", auth.getAuthToken());
            }
            http.setDoOutput(true);
            writeBody(request, http);
            http.connect();
            if (http.getResponseCode() != 200) {
                throw new Exception("Error Code " + http.getResponseCode());
            }
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0 ) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader((respBody));
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


}
