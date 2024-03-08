package dataAccess;
import java.util.HashMap;
import java.util.Map;
import model.AuthData;

public class MemoryAuthTokenDAO {

    static Map<String, AuthData> authMap = new HashMap<>();

    public void addToken(AuthData authToken) throws DataAccessException {
        authMap.put(authToken.getAuthToken(), authToken);
    }

    public AuthData findToken(String authToken) throws DataAccessException {
        return authMap.get(authToken);
    }

    public void removeToken(String authToken) throws DataAccessException {
        authMap.remove(authToken);
    }

    public void clearTokens() throws DataAccessException {
        authMap.clear();
    }

}
