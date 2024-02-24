package dataAccess;
import java.util.HashMap;
import java.util.Map;
import model.AuthData;

public class AuthTokenDAO {

    static Map<String, AuthData> authMap = new HashMap<>();

    public void addToken(AuthData authToken) {
        authMap.put(authToken.getAuthToken(), authToken);
    }

    public AuthData findToken(String authToken) {
        return authMap.get(authToken);
    }

    public void removeToken(String authToken) {
        authMap.remove(authToken);
    }

    public void clearTokens() throws DataAccessException {
        authMap.clear();
    }

}
