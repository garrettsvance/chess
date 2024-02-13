package dataAccess;
import model.UserData;
import java.util.HashMap;
import java.util.Map;


public class userDAO {

    static Map<String, UserData> userMap = new HashMap<>();

    public void insertUser(UserData userInfo) {
        userMap.put(userInfo.getUserName(), userInfo);
    }

    public UserData findUser(String userName) {
        return userMap.get(userName);
    }

    public void clearTokens() throws DataAccessException {
        userMap.clear();
    }

}
