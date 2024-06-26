package server;

import SharedServices.*;
import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class ServerFacade {

    private final String serverURL;

    public ServerFacade(String url) {
        serverURL = url;
    }


    public AuthData login(UserData user) {
        var request = new LoginRequest(user.getUserName(), user.getPassword(), user.getEmail());
        return this.makeRequest("POST", "/session", request, AuthData.class, null);
    }

    public AuthData register(UserData user) {
        var request = new RegisterRequest(user.getUserName(), user.getPassword(), user.getEmail());
        return this.makeRequest("POST", "/user", request, AuthData.class, null);
    }

    public void clearDatabase() {
        this.makeRequest("DELETE", "/db", null, null, null);
    }

    public void logout(AuthData authToken) {

        this.makeRequest("DELETE", "/session", null, null, authToken);
    }

    public ArrayList<GameData> listGames(AuthData authToken) {
        record ListGamesResponse(ArrayList<GameData> games) {}
        ListGamesResponse response = this.makeRequest("GET", "/game", null, ListGamesResponse.class, authToken);
        if (response.games() == null) {
            return null;
        }
        return response.games();
    }

    public GameData createGame(AuthData authToken, String gameName) {
        var request = new CreateGameRequest(gameName, null, null);
        return this.makeRequest("POST", "/game", request, GameData.class, authToken);
    }

    public ChessGame joinGame(AuthData authToken, String playerColor, Integer gameID) {
        String auth = authToken.getAuthToken();
        var request = new JoinGameRequest(playerColor, gameID, auth);
        return makeRequest("PUT", "/game", request, ChessGame.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, AuthData auth) {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            if (auth != null) {
                String authToken = auth.getAuthToken();
                http.setRequestProperty("Authorization", authToken);
            }
            http.setDoOutput(true);
            writeBody(request, http);
            http.connect();
            if (http.getResponseCode() != 200) {
                throw new Exception("Error Code " + http.getResponseCode());
            }
            return readBody(http, responseClass);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
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
                String readerString = new String(respBody.readAllBytes()); // changes here
                if (responseClass != null) {
                    response = new Gson().fromJson(readerString, responseClass);
                }
            }
        }
        return response;
    }


}
